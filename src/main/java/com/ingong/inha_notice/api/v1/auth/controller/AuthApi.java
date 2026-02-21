/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.api.v1.auth.controller;

import com.ingong.inha_notice.api.v1.auth.dto.request.jwt.RefreshTokenRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LogoutRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.LoginResponseDTO;
import com.ingong.inha_notice.global.api.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth API")
@RequestMapping("/api/v1/auth")
public interface AuthApi {

  @PostMapping("/join")
  ResponseEntity<ApiResponseDTO<JoinResponseDTO>> join(
      @RequestBody @Valid JoinRequestDTO joinRequestDTO);

  @PostMapping("/login")
  ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(
      @RequestBody @Valid LoginRequestDTO loginRequestDTO);

  @PostMapping("/refresh")
  ResponseEntity<ApiResponseDTO<RefreshTokenRequestDTO>> refresh(
      @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO,
      HttpServletRequest request, HttpServletResponse response
  );

  @PostMapping("/logout")
  ApiResponseDTO<Void> logout(
      @RequestBody LogoutRequestDTO logoutRequestDTO,
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      HttpServletResponse response
  );
}