/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RefreshTokenTest {

  @Nested
  @DisplayName("revoke 메서드는")
  class RevokeTest {

    @Test
    void 토큰을_취소_상태로_변경한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().plusDays(7))
          .build();

      // when
      token.revoke();

      // then
      assertThat(token.getIsRevoked()).isTrue();
      assertThat(token.getRevokedAt()).isNotNull();
    }
  }

  @Nested
  @DisplayName("updateLastUsedAt 메서드는")
  class UpdateLastUsedAtTest {

    @Test
    void 마지막_사용_시간을_업데이트한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().plusDays(7))
          .build();

      // when
      token.updateLastUsedAt();

      // then
      assertThat(token.getLastUsedAt()).isNotNull();
    }
  }

  @Nested
  @DisplayName("isExpired 메서드는")
  class IsExpiredTest {

    @Test
    void 만료된_토큰이면_true를_반환한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().minusDays(1)) // 어제 만료
          .build();

      // when
      boolean result = token.isExpired();

      // then
      assertThat(result).isTrue();
    }

    @Test
    void 만료되지_않은_토큰이면_false를_반환한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().plusDays(7)) // 7일 후 만료
          .build();

      // when
      boolean result = token.isExpired();

      // then
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("isValid 메서드는")
  class IsValidTest {

    @Test
    void 취소되지_않고_만료되지_않은_토큰이면_true를_반환한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().plusDays(7))
          .build();

      // when
      boolean result = token.isValid();

      // then
      assertThat(result).isTrue();
    }

    @Test
    void 취소된_토큰이면_false를_반환한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().plusDays(7))
          .build();

      token.revoke();

      // when
      boolean result = token.isValid();

      // then
      assertThat(result).isFalse();
    }

    @Test
    void 만료된_토큰이면_false를_반환한다() {
      // given
      User user = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .build();

      RefreshToken token = RefreshToken.builder()
          .user(user)
          .deviceId("device-123")
          .tokenHash("hash-value")
          .expiresAt(LocalDateTime.now().minusDays(1))
          .build();

      // when
      boolean result = token.isValid();

      // then
      assertThat(result).isFalse();
    }
  }
}
