/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.infra.jwt;

import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.domain.auth.infra.jwt.exception.JwtAuthenticationException;
import com.ingong.inha_notice.domain.auth.infra.jwt.status.JwtErrorStatus;
import com.ingong.inha_notice.global.error.BusinessException;
import com.ingong.inha_notice.global.security.auth.PublicIdUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final JwtProperties jwtProperties;
  private final PublicIdUserDetailsService publicIdUserDetailsService;

  private PrivateKey privateKey;
  private PublicKey publicKey;

  @PostConstruct
  public void init() {
    if (!StringUtils.hasText(jwtProperties.getPrivateKey())
        || !StringUtils.hasText(jwtProperties.getPublicKey())
        || !StringUtils.hasText(jwtProperties.getIssuer())
        || !StringUtils.hasText(jwtProperties.getAudience())
        || jwtProperties.getAccessToken() == null
        || jwtProperties.getRefreshToken() == null
        || jwtProperties.getAccessToken().getExpiration() <= 0
        || jwtProperties.getRefreshToken().getExpiration() <= 0) {
      throw new IllegalStateException("JWT 설정값이 누락되었습니다.");
    }

    try {
      // String으로 된 키를 RSA Key 객체로 변환
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");

      // Private Key (PKCS#8)
      byte[] privateKeyBytes = Base64.getDecoder().decode(jwtProperties.getPrivateKey());
      PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
      this.privateKey = keyFactory.generatePrivate(privateKeySpec);

      // Public Key (X.509)
      byte[] publicKeyBytes = Base64.getDecoder().decode(jwtProperties.getPublicKey());
      X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      this.publicKey = keyFactory.generatePublic(publicKeySpec);

    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("JWT 키가 올바른 Base64 형식이 아닙니다.", e);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("JWT 키 스펙/알고리즘 오류입니다.", e);
    }
  }

  public TokenResponseDTO issueTokenPair(String userPublicId) {
    long now = System.currentTimeMillis();

    long accessTokenExp = jwtProperties.getAccessToken().getExpiration();
    long refreshTokenExp = jwtProperties.getRefreshToken().getExpiration();

    String accessToken = buildToken(userPublicId, now, accessTokenExp, TOKEN_TYPE_ACCESS);
    String refreshToken = buildToken(userPublicId, now, refreshTokenExp, TOKEN_TYPE_REFRESH);

    return TokenResponseDTO.of(accessToken, refreshToken, accessTokenExp);
  }

  public UsernamePasswordAuthenticationToken getAuthentication(String rawAuthorizationHeader) {
    Claims claims = parseClaims(stripBearer(rawAuthorizationHeader), TOKEN_TYPE_ACCESS);

    String subject = claims.getSubject();
    if (!StringUtils.hasText(subject)) {
      throw new JwtAuthenticationException("토큰에 subject가 없습니다.");
    }

    UserDetails userDetails = publicIdUserDetailsService.loadUserByUsername(claims.getSubject());
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  public String extractPublicIdFromRefresh(String refreshToken) {
    if (!StringUtils.hasText(refreshToken)) {
      throw new BusinessException(JwtErrorStatus.JWT_REFRESH_TOKEN_NOT_FOUND);
    }

    Claims claims = parseClaims(refreshToken, TOKEN_TYPE_REFRESH);
    return claims.getSubject();
  }

  public TokenResponseDTO reissueAccessToken(String existingRefreshToken) {
    if (!StringUtils.hasText(existingRefreshToken)) {
      throw new BusinessException(JwtErrorStatus.JWT_REFRESH_TOKEN_NOT_FOUND);
    }

    Claims claims = parseClaims(existingRefreshToken, TOKEN_TYPE_REFRESH);
    String userPublicId = claims.getSubject();

    long now = System.currentTimeMillis();
    long accessTokenExp = jwtProperties.getAccessToken().getExpiration();
    String accessToken = buildToken(userPublicId, now, accessTokenExp, TOKEN_TYPE_ACCESS);
    return TokenResponseDTO.of(accessToken, existingRefreshToken, accessTokenExp);
  }

  private String buildToken(String userPublicId, long now, long expiration, String tokenType) {
    if (!StringUtils.hasText(userPublicId)) {
      throw new IllegalArgumentException("userPublicId가 없습니다.");
    }

    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(userPublicId)
        .issuer(jwtProperties.getIssuer())
        .audience().add(jwtProperties.getAudience()).and()
        .claim("token_type", tokenType)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expiration))
        .signWith(privateKey, Jwts.SIG.RS256)
        .compact();
  }

  private Claims parseClaims(String token, String tokenType) {
    if (!StringUtils.hasText(token)) {
      throw new JwtAuthenticationException("토큰이 없습니다.");
    }

    try {
      Jws<Claims> jws = Jwts.parser()
          .verifyWith(publicKey)
          .requireIssuer(jwtProperties.getIssuer())
          .requireAudience(jwtProperties.getAudience())
          .require("token_type", tokenType)
          .build()
          .parseSignedClaims(token);
      return jws.getPayload();
    } catch (ExpiredJwtException e) {
      throw new JwtAuthenticationException("만료된 토큰입니다.", e);
    } catch (MalformedJwtException | UnsupportedJwtException e) {
      throw new JwtAuthenticationException("잘못된 형식의 토큰입니다.", e);
    } catch (MissingClaimException | IncorrectClaimException e) {
      throw new JwtAuthenticationException("토큰 클레임이 유효하지 않습니다.", e);
    }
  }

  private String stripBearer(String rawToken) {
    if (!StringUtils.hasText(rawToken)) {
      throw new JwtAuthenticationException("Authorization 토큰이 없습니다.");
    }
    if (!rawToken.trim().regionMatches(true, 0, "Bearer ", 0, 7)) {
      throw new JwtAuthenticationException("Authorization 헤더는 Bearer 토큰 형식이어야 합니다.");
    }
    String token = rawToken.trim().substring(7).trim();
    if (token.isEmpty()) {
      throw new JwtAuthenticationException("Bearer 토큰이 없습니다.");
    }
    return token;
  }
}