/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.global.security.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PublicIdUserDetailsServiceTest {

  @InjectMocks
  private PublicIdUserDetailsService publicIdUserDetailsService;

  @Mock
  private UserRepository userRepository;

  @Nested
  @DisplayName("loadUserByUsername 메서드는")
  class LoadUserByUsernameTest {

    private final String validPublicId = "test-public-id-123";

    @Test
    void 존재하지않는_publicId면_예외를_던진다() {
      given(userRepository.findByPublicId(validPublicId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> publicIdUserDetailsService.loadUserByUsername(validPublicId))
          .isInstanceOf(UsernameNotFoundException.class)
          .hasMessageContaining("유저");

      then(userRepository).should().findByPublicId(validPublicId);
      then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 유효한_publicId로_UserDetails를_반환한다() {
      User validUser = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();

      given(userRepository.findByPublicId(validPublicId)).willReturn(Optional.of(validUser));

      UserDetails result = publicIdUserDetailsService.loadUserByUsername(validPublicId);

      assertThat(result).isNotNull();
      assertThat(result).isInstanceOf(AuthenticatedUser.class);

      then(userRepository).should().findByPublicId(validPublicId);
    }

    @Test
    void 반환된_UserDetails에_사용자_정보가_포함된다() {
      User validUser = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();

      given(userRepository.findByPublicId(validPublicId)).willReturn(Optional.of(validUser));

      UserDetails result = publicIdUserDetailsService.loadUserByUsername(validPublicId);

      assertThat(result.getPassword()).isEqualTo(validUser.getPassword());

      then(userRepository).should().findByPublicId(validPublicId);
    }
  }
}
