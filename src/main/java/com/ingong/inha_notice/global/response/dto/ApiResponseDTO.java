/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.global.response.dto;

import com.ingong.inha_notice.global.response.status.ErrorStatus;
import com.ingong.inha_notice.global.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApiResponseDTO<D>(
    @Schema(description = "성공한 요청이면 true, 그렇지 않으면 false를 반환한다.")
    boolean success,

    @Schema(description = "HTTP 상태 코드를 반환한다.")
    int status,

    @Schema(description = "커스텀 상태 코드를 반환한다 (예: [Domain]_[UseCase number]).")
    String code,

    @Schema(description = "결과에 대한 상태 메시지를 반환한다.")
    String message,

    @Schema(description = "결과 데이터를 반환한다.")
    D data
) {

  public static <D> ApiResponseDTO<D> success(SuccessStatus successStatus) {
    return new ApiResponseDTO<>(
        true,
        successStatus.getHttpStatus().value(),
        successStatus.getCode(),
        successStatus.getMessage(),
        null
    );
  }

  public static <D> ApiResponseDTO<D> success(SuccessStatus successStatus, D data) {
    return new ApiResponseDTO<>(
        true,
        successStatus.getHttpStatus().value(),
        successStatus.getCode(),
        successStatus.getMessage(),
        data
    );
  }

  public static <D> ApiResponseDTO<D> fail(ErrorStatus errorStatus) {
    return new ApiResponseDTO<>(
        false,
        errorStatus.getHttpStatus().value(),
        errorStatus.getCode(),
        errorStatus.getMessage(),
        null
    );
  }

  public static <D> ApiResponseDTO<D> fail(ErrorStatus errorStatus, D data) {
    return new ApiResponseDTO<>(
        false,
        errorStatus.getHttpStatus().value(),
        errorStatus.getCode(),
        errorStatus.getMessage(),
        data
    );
  }
}
