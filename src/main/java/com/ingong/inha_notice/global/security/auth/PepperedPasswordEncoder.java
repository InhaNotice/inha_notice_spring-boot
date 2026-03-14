/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.global.security.auth;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

public class PepperedPasswordEncoder implements PasswordEncoder {

  private static final String HMAC_SHA256 = "HmacSHA256";

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
    return delegate.encode(preHash(rawPassword));
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return delegate.matches(preHash(rawPassword), encodedPassword);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    return delegate.upgradeEncoding(encodedPassword);
  }

  private String preHash(CharSequence rawPassword) {
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec key = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
      mac.init(key);
      byte[] digest = mac.doFinal(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (Exception e) {
      throw new IllegalStateException("비밀번호 사전 해시 처리에 실패했습니다.", e);
    }
  }
}
