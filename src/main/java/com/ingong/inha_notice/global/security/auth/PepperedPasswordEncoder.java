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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

public class PepperedPasswordEncoder implements PasswordEncoder {

  private final PasswordEncoder delegate;
  private final String pepper;

  public PepperedPasswordEncoder(PasswordEncoder delegate, String pepper) {
    if (!StringUtils.hasText(pepper)) {
      throw new IllegalStateException("security.password.pepper 설정이 필요합니다.");
    }
    this.delegate = delegate;
    this.pepper = pepper;
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return delegate.encode(withPepper(rawPassword));
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return delegate.matches(withPepper(rawPassword), encodedPassword);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    return delegate.upgradeEncoding(encodedPassword);
  }

  private String withPepper(CharSequence rawPassword) {
    return rawPassword + pepper;
  }
}
