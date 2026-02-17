/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.domain.user.status;

import com.ingong.inha_notice.global.response.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessStatus implements SuccessStatus {
  MY_INFO_FETCH_SUCCESS(HttpStatus.OK, "USER_200_001", "내 정보 조회에 성공했어요.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
