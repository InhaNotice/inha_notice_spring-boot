/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.domain.auth.service;

import com.ingong.inha_notice.api.v1.auth.dto.request.local.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LogoutRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.LoginResponseDTO;
import com.ingong.inha_notice.domain.auth.infra.jwt.JwtTokenProvider;
import com.ingong.inha_notice.domain.auth.status.AuthErrorStatus;
import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import com.ingong.inha_notice.global.error.BusinessException;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
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

    TokenResponseDTO tokenResponseDTO = jwtTokenProvider.createTokens(savedUser.getPublicId());

    return new JoinResponseDTO(tokenResponseDTO, savedUser.getEmail(),
        savedUser.getIsPrivacyAgreed());
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

    TokenResponseDTO tokenResponseDTO = jwtTokenProvider.createTokens(user.getPublicId());

    return new LoginResponseDTO(tokenResponseDTO, user.getEmail());
  }

  public void logout(AuthenticatedUser authenticatedUser, LogoutRequestDTO logoutRequestDTO) {
    if (authenticatedUser == null) {
      throw new BusinessException(AuthErrorStatus.ACCESS_DENIED);
    }

    if (logoutRequestDTO.isAllDevices()) {

    }

  }
}