/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-18
 */

package com.ingong.inha_notice.api.v1.user.dto;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;

public record UserDTO(
    String publicId,
    String email,
    UserStatus status,
    UserRole role
) {

  public static UserDTO from(User user) {
    return new UserDTO(user.getPublicId(), user.getEmail(), user.getStatus(), user.getRole());
  }
}
