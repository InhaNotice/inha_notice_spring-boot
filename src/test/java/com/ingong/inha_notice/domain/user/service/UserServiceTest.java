/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ingong.inha_notice.api.v1.user.dto.response.UserInfoResponseDTO;
import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import com.ingong.inha_notice.global.error.BusinessException;
import com.ingong.inha_notice.global.error.GlobalErrorStatus;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Nested
  @DisplayName("getUserInfo 메서드는")
  class GetUserInfoTest {

    private final String validPublicId = "test-public-id-123";

    @Test
    void 존재하지않는_사용자면_예외를_던진다() {
      given(userRepository.findByPublicId(validPublicId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserInfo(validPublicId))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus()).isEqualTo(
              GlobalErrorStatus.RESOURCE_NOT_FOUND));

      then(userRepository).should().findByPublicId(validPublicId);
      then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 유효한_사용자정보조회가_진행된다() {
      User validUser = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();

      given(userRepository.findByPublicId(validPublicId)).willReturn(Optional.of(validUser));

      UserInfoResponseDTO result = userService.getUserInfo(validPublicId);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(validUser.getEmail());

      then(userRepository).should().findByPublicId(validPublicId);
    }
  }
}
