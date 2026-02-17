/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-16
 */

package com.ingong.inha_notice.global.api.status.auth;

import com.ingong.inha_notice.global.api.status.common.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessStatus implements SuccessStatus {

  // 200 OK
  LOCAL_LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200_001", "로그인에 성공했어요."),
  LOCAL_JOIN_SUCCESS(HttpStatus.OK, "AUTH_200_002", "회원가입에 성공했어요.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
