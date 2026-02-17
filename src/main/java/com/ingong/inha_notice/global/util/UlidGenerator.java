/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.global.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class UlidGenerator {

  private static final char[] ENCODING_CHARS =
      "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray(); // Crockford Base32
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final BigInteger BASE = BigInteger.valueOf(32);

  private UlidGenerator() {
  }

  public static String newUlid() {
    byte[] bytes = new byte[16];

    long time = System.currentTimeMillis(); // 48-bit timestamp (ms)
    bytes[0] = (byte) (time >>> 40);
    bytes[1] = (byte) (time >>> 32);
    bytes[2] = (byte) (time >>> 24);
    bytes[3] = (byte) (time >>> 16);
    bytes[4] = (byte) (time >>> 8);
    bytes[5] = (byte) (time);

    byte[] rand = new byte[10]; // 80-bit randomness
    RANDOM.nextBytes(rand);
    System.arraycopy(rand, 0, bytes, 6, 10);

    BigInteger value = new BigInteger(1, bytes);

    // ULID는 항상 26자리
    StringBuilder sb = new StringBuilder(26);
    for (int i = 0; i < 26; i++) {
      BigInteger[] divRem = value.divideAndRemainder(BASE);
      sb.append(ENCODING_CHARS[divRem[1].intValue()]);
      value = divRem[0];
    }
    return sb.reverse().toString();
  }
}
