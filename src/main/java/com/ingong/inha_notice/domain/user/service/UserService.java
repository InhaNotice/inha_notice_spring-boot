/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.domain.user.service;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import com.ingong.inha_notice.global.error.BusinessException;
import com.ingong.inha_notice.global.error.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  public User getByPublicId(String publicId) {
    return userRepository.findByPublicId(publicId)
        .orElseThrow(() -> new BusinessException(GlobalErrorStatus.RESOURCE_NOT_FOUND));
  }
}
