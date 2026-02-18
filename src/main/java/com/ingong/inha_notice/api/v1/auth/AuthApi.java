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
import com.ingong.inha_notice.global.response.dto.ApiResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
}