/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.domain.auth.infra.redis;

import com.ingong.inha_notice.global.redis.StringKeyValueStore;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenStore {

  private static final String KEY_PREFIX = "refresh:";

  private final StringKeyValueStore store;

  public void save(String userPublicId, String deviceId, String refreshTokenHash, Duration ttl) {
    store.set(key(userPublicId, deviceId), refreshTokenHash, ttl);
  }

  public Optional<String> find(String userPublicId, String deviceId) {
    return store.get(key(userPublicId, deviceId));
  }

  public void delete(String userPublicId, String deviceId) {
    store.delete(key(userPublicId, deviceId));
  }

  public void deleteAllByUserPublicId(String userPublicId) {
    store.deleteByPattern(KEY_PREFIX + userPublicId + ":*");
  }

  public String key(String userPublicId, String deviceId) {
    return KEY_PREFIX + userPublicId + ":" + deviceId;
  }
}
