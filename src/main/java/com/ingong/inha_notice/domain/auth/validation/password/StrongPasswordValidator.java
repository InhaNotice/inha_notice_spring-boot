/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

  private int min;
  private int max;
  private boolean requireUppercase;
  private boolean requireLowercase;
  private boolean requireDigit;
  private boolean requireSpecial;

  @Override
  public void initialize(StrongPassword constraintAnnotation) {
    this.min = constraintAnnotation.min();
    this.max = constraintAnnotation.max();
    this.requireUppercase = constraintAnnotation.requireUppercase();
    this.requireLowercase = constraintAnnotation.requireLowercase();
    this.requireDigit = constraintAnnotation.requireDigit();
    this.requireSpecial = constraintAnnotation.requireSpecial();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return true;
    }

    if (value.length() < min) {
      return false;
    }

    if (value.length() > max) {
      return false;
    }

    boolean hasUppercase = false;
    boolean hasLowercase = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;

    for (char ch : value.toCharArray()) {
      if (Character.isUpperCase(ch)) {
        hasUppercase = true;
        continue;
      }
      if (Character.isLowerCase(ch)) {
        hasLowercase = true;
        continue;
      }
      if (Character.isDigit(ch)) {
        hasDigit = true;
        continue;
      }
      if (!Character.isWhitespace(ch)) {
        hasSpecial = true;
      }
    }

    if (requireUppercase && !hasUppercase) {
      return false;
    }
    if (requireLowercase && !hasLowercase) {
      return false;
    }
    if (requireDigit && !hasDigit) {
      return false;
    }
    if (requireSpecial && !hasSpecial) {
      return false;
    }

    return true;
  }
}
