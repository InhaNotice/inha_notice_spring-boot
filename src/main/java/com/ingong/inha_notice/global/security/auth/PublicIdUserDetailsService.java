/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.global.security.auth;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicIdUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String publicId) throws UsernameNotFoundException {
    User user = userRepository.findByPublicId(publicId)
        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

    return AuthenticatedUser.from(user);
  }
}
