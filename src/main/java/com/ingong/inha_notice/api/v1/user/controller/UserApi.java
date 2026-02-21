/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.api.v1.user.controller;

import com.ingong.inha_notice.api.v1.user.dto.response.UserInfoResponseDTO;
import com.ingong.inha_notice.global.api.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User API")
@RequestMapping("/api/v1/user")
public interface UserApi {

  @PostMapping("/me")
  @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
  ResponseEntity<ApiResponseDTO<UserInfoResponseDTO>> getUserInfo(
      @AuthenticationPrincipal AuthenticatedUser userDetails);
}