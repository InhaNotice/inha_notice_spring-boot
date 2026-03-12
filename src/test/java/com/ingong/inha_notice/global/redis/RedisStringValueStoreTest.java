/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.global.redis;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class RedisStringValueStoreTest {

  private RedisStringValueStore redisStringValueStore;

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private StringKeyValueStore stringKeyValueStore;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @BeforeEach
  void setUp() {
    redisStringValueStore = new RedisStringValueStore(redisTemplate, stringKeyValueStore);
  }

  @Nested
  @DisplayName("set 메서드는")
  class SetTest {

    private final String key = "test-key";
    private final String value = "test-value";
    private final Duration ttl = Duration.ofMinutes(10);

    @Test
    void 값을_저장한다() {
      given(redisTemplate.opsForValue()).willReturn(valueOperations);

      redisStringValueStore.set(key, value, ttl);

      then(redisTemplate).should().opsForValue();
      then(valueOperations).should().set(key, value, ttl);
    }
  }

  @Nested
  @DisplayName("get 메서드는")
  class GetTest {

    private final String key = "test-key";
    private final String value = "test-value";

    @Test
    void 값이_존재하면_Optional로_반환한다() {
      given(redisTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.get(key)).willReturn(value);

      Optional<String> result = redisStringValueStore.get(key);

      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(value);

      then(redisTemplate).should().opsForValue();
      then(valueOperations).should().get(key);
    }

    @Test
    void 값이_없으면_Optional_empty를_반환한다() {
      given(redisTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.get(key)).willReturn(null);

      Optional<String> result = redisStringValueStore.get(key);

      assertThat(result).isEmpty();

      then(redisTemplate).should().opsForValue();
      then(valueOperations).should().get(key);
    }
  }

  @Nested
  @DisplayName("delete 메서드는")
  class DeleteTest {

    private final String key = "test-key";

    @Test
    void 키를_삭제한다() {
      redisStringValueStore.delete(key);

      then(redisTemplate).should().delete(key);
    }
  }

  @Nested
  @DisplayName("deleteByPattern 메서드는")
  class DeleteByPatternTest {

    private final String pattern = "test:*";

    @Test
    void 패턴과_일치하는_키들을_삭제한다() {
      Set<String> keys = Set.of("test:1", "test:2", "test:3");
      given(redisTemplate.keys(pattern)).willReturn(keys);

      redisStringValueStore.deleteByPattern(pattern);

      then(redisTemplate).should().keys(pattern);
      then(redisTemplate).should().delete(keys);
    }

    @Test
    void 일치하는_키가_없으면_삭제하지_않는다() {
      given(redisTemplate.keys(pattern)).willReturn(null);

      redisStringValueStore.deleteByPattern(pattern);

      then(redisTemplate).should().keys(pattern);
      then(redisTemplate).should(never()).delete(any(Set.class));
    }

    @Test
    void 일치하는_키가_빈_Set이면_삭제하지_않는다() {
      given(redisTemplate.keys(pattern)).willReturn(Collections.emptySet());

      redisStringValueStore.deleteByPattern(pattern);

      then(redisTemplate).should().keys(pattern);
      then(redisTemplate).should(never()).delete(any(Set.class));
    }
  }
}
