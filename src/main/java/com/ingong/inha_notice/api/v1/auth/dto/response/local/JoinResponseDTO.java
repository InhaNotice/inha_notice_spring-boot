/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.api.v1.auth.dto.response.local;

import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;

public record JoinResponseDTO(
    TokenResponseDTO tokenResponseDTO,
    String email,
    Boolean isPrivacyAgreed
) {

}
