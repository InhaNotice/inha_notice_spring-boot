/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.domain.auth.infra.jwt;

import com.ingong.inha_notice.domain.auth.dto.TokenResponseDTO;
import com.ingong.inha_notice.global.security.auth.PublicIdUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
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

  public TokenResponseDTO createTokens(String userPublicId) {
    long now = System.currentTimeMillis();

    // Access Token 생성
    long accessTokenExp = jwtProperties.getAccessToken().getExpiration();
    String accessToken = Jwts.builder()
        // Payload
        .id(UUID.randomUUID().toString()) // jti (JWT ID)
        .subject(userPublicId) // sub (User Public ID)
        .issuer(jwtProperties.getIssuer()) // iss
        .audience().add(jwtProperties.getAudience()).and() // aud
        .claim("token_type", "access") // token_type
        .issuedAt(new Date(now)) // iat (Issued At)
        .expiration(new Date(now + accessTokenExp))// exp (Expiration)
        // Header + Signature generation rule
        .signWith(privateKey, Jwts.SIG.RS256) // alg=RS256, and create signature at compact()
        .compact();

    // Refresh Token 생성
    long refreshTokenExp = jwtProperties.getRefreshToken().getExpiration();
    String refreshToken = Jwts.builder()
        // Payload
        .id(UUID.randomUUID().toString())
        .subject(userPublicId)
        .issuer(jwtProperties.getIssuer()) // iss
        .audience().add(jwtProperties.getAudience()).and() // aud
        .claim("token_type", "refresh")
        .issuedAt(new Date(now))
        .expiration(new Date(now + refreshTokenExp))
        // Header + Signature generation rule
        .signWith(privateKey, Jwts.SIG.RS256)
        .compact();

    return TokenResponseDTO.of(accessToken, refreshToken, accessTokenExp);
  }

  public UsernamePasswordAuthenticationToken getAuthentication(String rawAuthorizationHeader) {
    Claims claims = parseAccessClaims(rawAuthorizationHeader);

    UserDetails userDetails = publicIdUserDetailsService.loadUserByUsername(claims.getSubject());
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  private Claims parseAccessClaims(String rawToken) {
    String token = stripBearer(rawToken);
    Jws<Claims> jws = Jwts.parser()
        .verifyWith(publicKey)
        .requireIssuer(jwtProperties.getIssuer())
        .requireAudience(jwtProperties.getAudience())
        .require("token_type", "access")
        .build()
        .parseSignedClaims(token);

    return jws.getPayload();
  }

  private String stripBearer(String rawToken) {
    if (rawToken == null) {
      throw new IllegalArgumentException("Authorization 토큰이 없습니다.");
    }

    String t = rawToken.trim();
    if (t.isEmpty()) {
      throw new IllegalArgumentException("Authorization 토큰이 없습니다.");
    }

    if (!t.regionMatches(true, 0, "Bearer ", 0, 7)) {
      throw new IllegalArgumentException("Authorization 헤더는 Bearer 토큰 형식이어야 합니다.");
    }

    String token = t.substring(7).trim();
    if (token.isEmpty()) {
      throw new IllegalArgumentException("Bearer 토큰이 없습니다.");
    }
    return token;
  }
}