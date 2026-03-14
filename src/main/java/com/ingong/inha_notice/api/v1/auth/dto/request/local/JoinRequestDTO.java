/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.api.v1.auth.dto.request.local;

import com.ingong.inha_notice.domain.auth.validation.password.StrongPassword;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JoinRequestDTO(
    @NotBlank
    @Email
    String email,

    @NotBlank
    @StrongPassword(
        message = "비밀번호는 10~72자이며 영문 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    String password,

    @NotNull
    @AssertTrue
    Boolean isPrivacyAgreed
) {

}
