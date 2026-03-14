/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.api.v1.auth.controller;

import com.ingong.inha_notice.api.v1.auth.dto.request.jwt.RefreshTokenRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LogoutRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.LoginResponseDTO;
import com.ingong.inha_notice.domain.auth.service.AuthService;
import com.ingong.inha_notice.domain.auth.status.AuthSuccessStatus;
import com.ingong.inha_notice.global.api.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;

  @Override
  public ResponseEntity<ApiResponseDTO<JoinResponseDTO>> join(
      @RequestBody @Valid JoinRequestDTO joinRequestDTO) {

    JoinResponseDTO responseDTO = authService.join(joinRequestDTO);
    AuthSuccessStatus status = AuthSuccessStatus.LOCAL_JOIN_SUCCESS;

    return ResponseEntity
        .status(status.getHttpStatus())
        .body(ApiResponseDTO.success(status, responseDTO));
  }

  @Override
  public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(
      @RequestBody @Valid LoginRequestDTO loginRequestDTO) {

    LoginResponseDTO responseDTO = authService.login(loginRequestDTO);
    AuthSuccessStatus status = AuthSuccessStatus.LOCAL_LOGIN_SUCCESS;

    return ResponseEntity
        .status(status.getHttpStatus())
        .body(ApiResponseDTO.success(status, responseDTO));
  }

  @Override
  public ResponseEntity<ApiResponseDTO<TokenResponseDTO>> refresh(
      @RequestBody @Valid RefreshTokenRequestDTO refreshTokenRequestDTO) {

    TokenResponseDTO responseDTO = authService.refresh(refreshTokenRequestDTO);
    AuthSuccessStatus status = AuthSuccessStatus.TOKEN_REFRESH_SUCCESS;

    return ResponseEntity
        .status(status.getHttpStatus())
        .body(ApiResponseDTO.success(status, responseDTO));
  }

  @Override
  public ApiResponseDTO<Void> logout(
      @RequestBody @Valid LogoutRequestDTO logoutRequestDTO,
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

    authService.logout(authenticatedUser, logoutRequestDTO);
    AuthSuccessStatus status = AuthSuccessStatus.LOGOUT_SUCCESS;

    return ApiResponseDTO.success(status);
  }
}