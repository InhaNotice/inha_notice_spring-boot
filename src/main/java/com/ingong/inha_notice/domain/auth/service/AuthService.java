/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.service;

import com.ingong.inha_notice.api.v1.auth.dto.request.jwt.RefreshTokenRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LogoutRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.LoginResponseDTO;
import com.ingong.inha_notice.api.v1.user.dto.response.UserInfoResponseDTO;
import com.ingong.inha_notice.domain.auth.infra.jwt.JwtProperties;
import com.ingong.inha_notice.domain.auth.infra.jwt.JwtTokenProvider;
import com.ingong.inha_notice.domain.auth.infra.redis.RedisRefreshTokenStore;
import com.ingong.inha_notice.domain.auth.status.AuthErrorStatus;
import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import com.ingong.inha_notice.global.error.BusinessException;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisRefreshTokenStore redisRefreshTokenStore;
  private final JwtProperties jwtProperties;

  @Transactional
  public JoinResponseDTO join(JoinRequestDTO dto) {
    if (userRepository.existsByEmail(dto.email())) {
      throw new BusinessException(AuthErrorStatus.EMAIL_ALREADY_EXISTS);
    }

    String encodedPassword = passwordEncoder.encode(dto.password());

    User newUser = User.builder()
        .email(dto.email())
        .password(encodedPassword)
        .isPrivacyAgreed(dto.isPrivacyAgreed())
        .status(UserStatus.ACTIVE)
        .build();

    User savedUser;
    try {
      savedUser = userRepository.save(newUser);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(AuthErrorStatus.EMAIL_ALREADY_EXISTS);
    }

    return new JoinResponseDTO(savedUser.getEmail(), savedUser.getIsPrivacyAgreed());
  }

  public LoginResponseDTO login(LoginRequestDTO dto) {
    User user = userRepository.findByEmail(dto.email())
        .orElseThrow(() -> new BusinessException(AuthErrorStatus.LOGIN_FAILED));

    if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
      throw new BusinessException(AuthErrorStatus.LOGIN_FAILED);
    }

    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new BusinessException(AuthErrorStatus.LOGIN_FAILED);
    }

    TokenResponseDTO tokenResponseDTO = jwtTokenProvider.issueTokenPair(user.getPublicId());

    // Refresh token을 Redis에 저장
    String refreshTokenHash = hashToken(tokenResponseDTO.refreshToken());
    Duration ttl = Duration.ofMillis(jwtProperties.getRefreshToken().getExpiration());
    redisRefreshTokenStore.save(user.getPublicId(), dto.deviceId(), refreshTokenHash, ttl);

    UserInfoResponseDTO userInfo = UserInfoResponseDTO.from(user);

    return new LoginResponseDTO(tokenResponseDTO, userInfo);
  }

  public TokenResponseDTO refresh(RefreshTokenRequestDTO dto) {
    // 1. Refresh token 검증 및 publicId 추출
    String publicId;
    try {
      publicId = jwtTokenProvider.extractPublicIdFromRefresh(dto.refreshToken());
    } catch (Exception e) {
      throw new BusinessException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
    }

    // 2. Redis에서 저장된 refresh token hash 조회
    String storedHash = redisRefreshTokenStore.find(publicId, dto.deviceId())
        .orElseThrow(() -> new BusinessException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND));

    // 3. 요청받은 refresh token을 해시화하여 Redis 값과 비교
    String requestHash = hashToken(dto.refreshToken());
    if (!storedHash.equals(requestHash)) {
      throw new BusinessException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
    }

    // 4. 새로운 access token 발급 (refresh token 재사용)
    return jwtTokenProvider.reissueAccessToken(dto.refreshToken());
  }

  public void logout(AuthenticatedUser authenticatedUser, LogoutRequestDTO logoutRequestDTO) {
    if (authenticatedUser == null) {
      throw new BusinessException(AuthErrorStatus.ACCESS_DENIED);
    }

    String publicId = authenticatedUser.getPublicId();

    if (logoutRequestDTO.isAllDevices()) {
      // 모든 디바이스에서 로그아웃
      redisRefreshTokenStore.deleteAllByUserPublicId(publicId);
    } else {
      // 특정 디바이스만 로그아웃
      redisRefreshTokenStore.delete(publicId, logoutRequestDTO.deviceId());
    }
  }

  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
    }
  }
}