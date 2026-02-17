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

public record FieldErrorDetailDTO(
    String field,
    String value,
    String reason
) {

}