/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.domain.user.status;

import com.ingong.inha_notice.global.api.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements ErrorStatus {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_001", "존재하지 않는 사용자입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
