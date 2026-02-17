/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 토큰 응답 DTO")
public record TokenResponseDTO(
    String accessToken,

    String refreshToken,

    @Schema(description = "토큰 타입", example = "Bearer")
    String grantType,

    @Schema(description = "액세스 토큰 만료 시간 (밀리초)")
    long expiresIn
) {

  public TokenResponseDTO {
    if (grantType == null || grantType.isBlank()) {
      grantType = "Bearer";
    }
  }

  public static TokenResponseDTO of(String accessToken, String refreshToken, long expiresIn) {
    return new TokenResponseDTO(accessToken, refreshToken, "Bearer", expiresIn);
  }
}
