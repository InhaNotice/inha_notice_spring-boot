/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.global.error;

import com.ingong.inha_notice.global.response.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorStatus implements ErrorStatus {
  // Http / Protocol
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "GLOBAL_400", "잘못된 요청입니다."),
  INVALID_METHOD_TYPE(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL_405", "허용되지 않는 HTTP 메서드입니다."),
  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "GLOBAL_415",
      "지원하지 않는 미디어 타입입니다."),
  // Security
  UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "GLOBAL_401", "인증이 필요합니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "GLOBAL_403", "권한이 없습니다."),
  // Infra / Server
  TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "GLOBAL_429", "요청이 너무 많습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_500", "서버 오류가 발생했습니다."),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL_404", "자원이 존재하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
