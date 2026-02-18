/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-18
 */

package com.ingong.inha_notice.api.v1.auth;

import com.ingong.inha_notice.api.v1.auth.dto.local.request.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.local.request.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.local.response.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.local.response.LoginResponseDTO;
import com.ingong.inha_notice.domain.auth.service.AuthService;
import com.ingong.inha_notice.domain.auth.status.AuthSuccessStatus;
import com.ingong.inha_notice.global.response.dto.ApiResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;

  @Override
  public ResponseEntity<ApiResponseDTO<JoinResponseDTO>> join(
      @RequestBody @Valid JoinRequestDTO joinRequestDTO) {
    JoinResponseDTO joinResponseDTO = authService.join(joinRequestDTO);
    AuthSuccessStatus status = AuthSuccessStatus.LOCAL_JOIN_SUCCESS;
    return ResponseEntity
        .status(AuthSuccessStatus.LOCAL_JOIN_SUCCESS.getHttpStatus())
        .body(ApiResponseDTO.success(status, joinResponseDTO));
  }

  @Override
  public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(
      @RequestBody @Valid LoginRequestDTO loginRequestDTO) {
    LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
    AuthSuccessStatus status = AuthSuccessStatus.LOCAL_LOGIN_SUCCESS;
    return ResponseEntity
        .status(status.getHttpStatus())
        .body(ApiResponseDTO.success(status, loginResponseDTO));
  }
}