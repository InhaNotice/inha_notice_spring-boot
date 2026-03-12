/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.domain.user.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UlidUtilTest {

  @Nested
  @DisplayName("getNewUlid 메서드는")
  class GetNewUlidTest {

    @Test
    void ULID를_생성한다() {
      String ulid = UlidUtil.getNewUlid();

      assertThat(ulid).isNotNull();
    }

    @Test
    void 생성된_ULID는_26자이다() {
      String ulid = UlidUtil.getNewUlid();

      assertThat(ulid.length()).isEqualTo(26);
    }

    @Test
    void 생성된_ULID는_Crockford_Base32_문자만_포함한다() {
      String ulid = UlidUtil.getNewUlid();
      String validChars = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

      for (char c : ulid.toCharArray()) {
        assertThat(validChars).contains(String.valueOf(c));
      }
    }

    @Test
    void 여러번_호출해도_중복되지_않는다() {
      Set<String> ulids = new HashSet<>();

      for (int i = 0; i < 1000; i++) {
        String ulid = UlidUtil.getNewUlid();
        ulids.add(ulid);
      }

      // 1000개 생성했을 때 모두 unique해야 함
      assertThat(ulids.size()).isEqualTo(1000);
    }

    @Test
    void 연속_호출시_타임스탬프_순서로_정렬가능하다() throws InterruptedException {
      String ulid1 = UlidUtil.getNewUlid();
      Thread.sleep(1); // 1ms 대기 (타임스탬프 차이 보장)
      String ulid2 = UlidUtil.getNewUlid();

      // ULID는 타임스탬프가 앞부분이므로 사전순으로 정렬 가능
      assertThat(ulid1.compareTo(ulid2)).isLessThan(0);
    }

    @Test
    void 대문자만_포함한다() {
      String ulid = UlidUtil.getNewUlid();

      for (char c : ulid.toCharArray()) {
        if (Character.isLetter(c)) {
          assertThat(Character.isUpperCase(c)).isTrue();
        }
      }
    }

    @Test
    void 혼동하기_쉬운_문자를_제외한다() {
      String ulid = UlidUtil.getNewUlid();

      // Crockford Base32는 I, L, O, U 제외 (0, 1과 혼동 방지)
      assertThat(ulid).doesNotContain("I");
      assertThat(ulid).doesNotContain("L");
      assertThat(ulid).doesNotContain("O");
      assertThat(ulid).doesNotContain("U");
    }
  }
}
