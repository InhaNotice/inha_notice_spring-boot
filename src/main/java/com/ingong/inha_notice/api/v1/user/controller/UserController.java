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
import com.ingong.inha_notice.domain.user.service.UserService;
import com.ingong.inha_notice.domain.user.status.UserSuccessStatus;
import com.ingong.inha_notice.global.api.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;

  @Override
  public ResponseEntity<ApiResponseDTO<UserInfoResponseDTO>> getUserInfo(
      @AuthenticationPrincipal AuthenticatedUser userDetails) {

    UserInfoResponseDTO responseDTO = userService.getUserInfo(userDetails.getPublicId());
    UserSuccessStatus status = UserSuccessStatus.USER_INFO_SUCCESS;

    return ResponseEntity
        .status(UserSuccessStatus.USER_INFO_SUCCESS.getHttpStatus())
        .body(ApiResponseDTO.success(status, responseDTO));
  }
}