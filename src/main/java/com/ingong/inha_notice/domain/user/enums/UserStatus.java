/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.domain.user.enums;

public enum UserStatus {
  ACTIVE,    // 활성
  DORMANT,   // 휴면
  BANNED,    // 정지
  WAITING_VERIFICATION // 이메일 인증 대기 (옵션)
}