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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PepperedPasswordEncoderTest {

  @Mock
  private PasswordEncoder delegate;

  private final String validPepper = "test-pepper-secret";

  @Nested
  @DisplayName("생성자는")
  class ConstructorTest {

    @Test
    void pepper가_null이면_예외를_던진다() {
      assertThatThrownBy(() -> new PepperedPasswordEncoder(delegate, null))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("pepper");
    }

    @Test
    void pepper가_빈문자열이면_예외를_던진다() {
      assertThatThrownBy(() -> new PepperedPasswordEncoder(delegate, ""))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("pepper");
    }

    @Test
    void pepper가_공백만있으면_예외를_던진다() {
      assertThatThrownBy(() -> new PepperedPasswordEncoder(delegate, "   "))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("pepper");
    }

    @Test
    void 유효한_pepper로_인코더를_생성한다() {
      PepperedPasswordEncoder encoder = new PepperedPasswordEncoder(delegate, validPepper);

      assertThat(encoder).isNotNull();
    }
  }

  @Nested
  @DisplayName("encode 메서드는")
  class EncodeTest {

    private PepperedPasswordEncoder encoder;

    @Test
    void 비밀번호를_암호화한다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String rawPassword = "password123";
      String pepperedPassword = rawPassword + validPepper;
      String encodedPassword = "encoded-password-hash";

      given(delegate.encode(pepperedPassword)).willReturn(encodedPassword);

      String result = encoder.encode(rawPassword);

      assertThat(result).isEqualTo(encodedPassword);

      then(delegate).should().encode(pepperedPassword);
    }

    @Test
    void pepper가_추가되어_암호화된다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String rawPassword = "test123";
      String pepperedPassword = rawPassword + validPepper;

      given(delegate.encode(anyString())).willReturn("encoded");

      encoder.encode(rawPassword);

      then(delegate).should().encode(pepperedPassword);
    }
  }

  @Nested
  @DisplayName("matches 메서드는")
  class MatchesTest {

    private PepperedPasswordEncoder encoder;

    @Test
    void 같은_비밀번호면_매칭에_성공한다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String rawPassword = "password123";
      String pepperedPassword = rawPassword + validPepper;
      String encodedPassword = "encoded-hash";

      given(delegate.matches(pepperedPassword, encodedPassword)).willReturn(true);

      boolean result = encoder.matches(rawPassword, encodedPassword);

      assertThat(result).isTrue();

      then(delegate).should().matches(pepperedPassword, encodedPassword);
    }

    @Test
    void 다른_비밀번호면_매칭에_실패한다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String rawPassword = "password123";
      String pepperedPassword = rawPassword + validPepper;
      String wrongEncodedPassword = "wrong-encoded-hash";

      given(delegate.matches(pepperedPassword, wrongEncodedPassword)).willReturn(false);

      boolean result = encoder.matches(rawPassword, wrongEncodedPassword);

      assertThat(result).isFalse();

      then(delegate).should().matches(pepperedPassword, wrongEncodedPassword);
    }

    @Test
    void pepper가_추가되어_매칭된다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String rawPassword = "test123";
      String pepperedPassword = rawPassword + validPepper;
      String encodedPassword = "encoded";

      given(delegate.matches(anyString(), anyString())).willReturn(true);

      encoder.matches(rawPassword, encodedPassword);

      then(delegate).should().matches(pepperedPassword, encodedPassword);
    }
  }

  @Nested
  @DisplayName("upgradeEncoding 메서드는")
  class UpgradeEncodingTest {

    private PepperedPasswordEncoder encoder;

    @Test
    void delegate에_위임한다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String encodedPassword = "encoded-hash";

      given(delegate.upgradeEncoding(encodedPassword)).willReturn(false);

      boolean result = encoder.upgradeEncoding(encodedPassword);

      assertThat(result).isFalse();

      then(delegate).should().upgradeEncoding(encodedPassword);
    }

    @Test
    void delegate가_true를_반환하면_true를_반환한다() {
      encoder = new PepperedPasswordEncoder(delegate, validPepper);
      String encodedPassword = "old-encoded-hash";

      given(delegate.upgradeEncoding(encodedPassword)).willReturn(true);

      boolean result = encoder.upgradeEncoding(encodedPassword);

      assertThat(result).isTrue();

      then(delegate).should().upgradeEncoding(encodedPassword);
    }
  }
}
