/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.domain.auth.infra.redis;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ingong.inha_notice.global.redis.StringKeyValueStore;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RedisRefreshTokenStoreTest {

  @InjectMocks
  private RedisRefreshTokenStore redisRefreshTokenStore;

  @Mock
  private StringKeyValueStore store;

  private final String userPublicId = "user-123";
  private final String deviceId = "device-abc";
  private final String refreshTokenHash = "hashed-refresh-token";
  private final Duration ttl = Duration.ofDays(7);

  @Nested
  @DisplayName("save 메서드는")
  class SaveTest {

    @Test
    void 토큰을_저장한다() {
      String expectedKey = "refresh:" + userPublicId + ":" + deviceId;

      redisRefreshTokenStore.save(userPublicId, deviceId, refreshTokenHash, ttl);

      then(store).should().set(expectedKey, refreshTokenHash, ttl);
    }

    @Test
    void 올바른_키_형식으로_저장한다() {
      String expectedKey = redisRefreshTokenStore.key(userPublicId, deviceId);

      redisRefreshTokenStore.save(userPublicId, deviceId, refreshTokenHash, ttl);

      then(store).should().set(expectedKey, refreshTokenHash, ttl);
    }
  }

  @Nested
  @DisplayName("find 메서드는")
  class FindTest {

    @Test
    void 토큰이_존재하면_반환한다() {
      String expectedKey = "refresh:" + userPublicId + ":" + deviceId;
      given(store.get(expectedKey)).willReturn(Optional.of(refreshTokenHash));

      Optional<String> result = redisRefreshTokenStore.find(userPublicId, deviceId);

      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(refreshTokenHash);

      then(store).should().get(expectedKey);
    }

    @Test
    void 토큰이_없으면_Optional_empty를_반환한다() {
      String expectedKey = "refresh:" + userPublicId + ":" + deviceId;
      given(store.get(expectedKey)).willReturn(Optional.empty());

      Optional<String> result = redisRefreshTokenStore.find(userPublicId, deviceId);

      assertThat(result).isEmpty();

      then(store).should().get(expectedKey);
    }

    @Test
    void 올바른_키로_조회한다() {
      String expectedKey = redisRefreshTokenStore.key(userPublicId, deviceId);
      given(store.get(expectedKey)).willReturn(Optional.of(refreshTokenHash));

      redisRefreshTokenStore.find(userPublicId, deviceId);

      then(store).should().get(expectedKey);
    }
  }

  @Nested
  @DisplayName("delete 메서드는")
  class DeleteTest {

    @Test
    void 토큰을_삭제한다() {
      String expectedKey = "refresh:" + userPublicId + ":" + deviceId;

      redisRefreshTokenStore.delete(userPublicId, deviceId);

      then(store).should().delete(expectedKey);
    }

    @Test
    void 올바른_키로_삭제한다() {
      String expectedKey = redisRefreshTokenStore.key(userPublicId, deviceId);

      redisRefreshTokenStore.delete(userPublicId, deviceId);

      then(store).should().delete(expectedKey);
    }
  }

  @Nested
  @DisplayName("deleteAllByUserPublicId 메서드는")
  class DeleteAllByUserPublicIdTest {

    @Test
    void 사용자의_모든_토큰을_삭제한다() {
      String expectedPattern = "refresh:" + userPublicId + ":*";

      redisRefreshTokenStore.deleteAllByUserPublicId(userPublicId);

      then(store).should().deleteByPattern(expectedPattern);
    }

    @Test
    void 올바른_패턴으로_삭제한다() {
      redisRefreshTokenStore.deleteAllByUserPublicId(userPublicId);

      then(store).should().deleteByPattern("refresh:" + userPublicId + ":*");
    }
  }

  @Nested
  @DisplayName("key 메서드는")
  class KeyTest {

    @Test
    void 올바른_키_형식을_생성한다() {
      String result = redisRefreshTokenStore.key(userPublicId, deviceId);

      assertThat(result).isEqualTo("refresh:user-123:device-abc");
    }

    @Test
    void prefix가_포함된_키를_생성한다() {
      String result = redisRefreshTokenStore.key(userPublicId, deviceId);

      assertThat(result).startsWith("refresh:");
    }

    @Test
    void userPublicId와_deviceId가_포함된_키를_생성한다() {
      String result = redisRefreshTokenStore.key(userPublicId, deviceId);

      assertThat(result).contains(userPublicId);
      assertThat(result).contains(deviceId);
    }
  }
}
