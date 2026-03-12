/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.domain.auth.validation.password;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StrongPasswordValidatorTest {

  private StrongPasswordValidator validator;

  @Mock
  private StrongPassword annotation;

  @Mock
  private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator = new StrongPasswordValidator();

    // 기본값 설정 (어노테이션 기본값과 동일)
    given(annotation.min()).willReturn(10);
    given(annotation.max()).willReturn(72);
    given(annotation.requireUppercase()).willReturn(true);
    given(annotation.requireLowercase()).willReturn(true);
    given(annotation.requireDigit()).willReturn(true);
    given(annotation.requireSpecial()).willReturn(true);

    validator.initialize(annotation);
  }

  @Nested
  @DisplayName("isValid 메서드는")
  class IsValidTest {

    @Test
    void null_값이면_true를_반환한다() {
      boolean result = validator.isValid(null, context);

      assertThat(result).isTrue();
    }

    @Test
    void 빈_문자열이면_true를_반환한다() {
      boolean result = validator.isValid("", context);

      assertThat(result).isTrue();
    }

    @Test
    void 공백만_있으면_true를_반환한다() {
      boolean result = validator.isValid("   ", context);

      assertThat(result).isTrue();
    }

    @Test
    void 최소길이_미만이면_false를_반환한다() {
      String shortPassword = "Aa1!"; // 4자 (최소 10자 필요)

      boolean result = validator.isValid(shortPassword, context);

      assertThat(result).isFalse();
    }

    @Test
    void 최대길이_초과이면_false를_반환한다() {
      String longPassword = "A".repeat(73) + "a1!"; // 73자 초과 (최대 72자)

      boolean result = validator.isValid(longPassword, context);

      assertThat(result).isFalse();
    }

    @Test
    void 대문자가_없으면_false를_반환한다() {
      String password = "password123!"; // 소문자+숫자+특수문자만

      boolean result = validator.isValid(password, context);

      assertThat(result).isFalse();
    }

    @Test
    void 소문자가_없으면_false를_반환한다() {
      String password = "PASSWORD123!"; // 대문자+숫자+특수문자만

      boolean result = validator.isValid(password, context);

      assertThat(result).isFalse();
    }

    @Test
    void 숫자가_없으면_false를_반환한다() {
      String password = "Password!!!"; // 대소문자+특수문자만

      boolean result = validator.isValid(password, context);

      assertThat(result).isFalse();
    }

    @Test
    void 특수문자가_없으면_false를_반환한다() {
      String password = "Password123"; // 대소문자+숫자만

      boolean result = validator.isValid(password, context);

      assertThat(result).isFalse();
    }

    @Test
    void 모든_조건을_만족하면_true를_반환한다() {
      String validPassword = "Password123!"; // 대소문자+숫자+특수문자, 12자

      boolean result = validator.isValid(validPassword, context);

      assertThat(result).isTrue();
    }

    @Test
    void 최소길이에_맞고_모든_조건_만족하면_true를_반환한다() {
      String validPassword = "Pass1234!!"; // 정확히 10자

      boolean result = validator.isValid(validPassword, context);

      assertThat(result).isTrue();
    }

    @Test
    void 최대길이에_맞고_모든_조건_만족하면_true를_반환한다() {
      String validPassword = "A".repeat(68) + "a1!"; // 정확히 72자

      boolean result = validator.isValid(validPassword, context);

      assertThat(result).isTrue();
    }
  }

  @Nested
  @DisplayName("initialize 메서드는")
  class InitializeTest {

    @Test
    void 어노테이션_속성을_올바르게_설정한다() {
      StrongPasswordValidator newValidator = new StrongPasswordValidator();

      given(annotation.min()).willReturn(8);
      given(annotation.max()).willReturn(20);
      given(annotation.requireUppercase()).willReturn(false);
      given(annotation.requireLowercase()).willReturn(true);
      given(annotation.requireDigit()).willReturn(true);
      given(annotation.requireSpecial()).willReturn(false);

      newValidator.initialize(annotation);

      // 대문자 불필요, 특수문자 불필요, 8-20자, 소문자+숫자 필요
      String password = "password123"; // 11자, 소문자+숫자

      boolean result = newValidator.isValid(password, context);

      assertThat(result).isTrue();
    }
  }
}
