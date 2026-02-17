/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.api.v1.user;

import com.ingong.inha_notice.api.v1.user.dto.response.UserInfoResponseDTO;
import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.service.UserService;
import com.ingong.inha_notice.domain.user.status.UserSuccessStatus;
import com.ingong.inha_notice.global.response.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
  @GetMapping("/me")
  public ApiResponseDTO<UserInfoResponseDTO> getMyInfo(
      @AuthenticationPrincipal AuthenticatedUser userDetails) {

    User user = userService.getByPublicId(userDetails.getPublicId());
    UserInfoResponseDTO userInfo = UserInfoResponseDTO.from(user);

    return ApiResponseDTO.success(UserSuccessStatus.MY_INFO_FETCH_SUCCESS, userInfo);
  }
}