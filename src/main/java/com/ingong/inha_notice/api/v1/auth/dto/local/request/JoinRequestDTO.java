/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-16
 */

package com.ingong.inha_notice.api.v1.auth.dto.local;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JoinRequestDTO(
    @NotBlank
    @Email
    String email,

    @NotBlank
    String password,

    @NotNull
    @AssertTrue
    Boolean isPrivacyAgreed
) {

}
