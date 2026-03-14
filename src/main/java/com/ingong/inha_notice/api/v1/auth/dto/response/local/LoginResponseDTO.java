/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.api.v1.auth.dto.response.local;

import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.api.v1.user.dto.response.UserInfoResponseDTO;

public record LoginResponseDTO(
    TokenResponseDTO tokens,
    UserInfoResponseDTO user
) {

}
