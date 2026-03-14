/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.status;

import com.ingong.inha_notice.global.api.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements ErrorStatus {
  // Accounts
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_409_001", "이미 사용 중인 이메일입니다."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_401_002", "아이디 또는 비밀번호가 일치하지 않습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_404_003", "존재하지 않는 사용자입니다."),

  // Authentication(401)
  EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_010", "인증 토큰이 제공되지 않았습니다."),
  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_011", "만료된 액세스 토큰입니다."),
  EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_012", "만료된 리프레시 토큰입니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_013", "유효하지 않은 토큰입니다."),
  INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH_401_014", "토큰의 서명이 올바르지 않습니다."),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_015", "지원하지 않는 형식의 토큰입니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_016", "유효하지 않은 리프레시 토큰입니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_401_017",
      "이미 로그아웃되었거나 존재하지 않는 리프레시 토큰입니다."),

  // Authorization(403)
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_403_020", "해당 자원에 접근할 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
