/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.scheduler;

import com.ingong.inha_notice.domain.auth.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

  private final RefreshTokenRepository refreshTokenRepository;

  // 매일 새벽 3시에 만료된 토큰 및 취소된 토큰 삭제
  @Scheduled(cron = "0 0 3 * * *")
  @Transactional
  public void cleanupExpiredAndRevokedTokens() {
    log.info("Starting cleanup of expired and revoked refresh tokens");

    LocalDateTime now = LocalDateTime.now();

    // 만료된 토큰 삭제
    int expiredCount = refreshTokenRepository.deleteExpiredTokens(now);
    log.info("Deleted {} expired refresh tokens", expiredCount);

    // 취소된 토큰 삭제
    int revokedCount = refreshTokenRepository.deleteRevokedTokens();
    log.info("Deleted {} revoked refresh tokens", revokedCount);

    log.info("Cleanup completed. Total deleted: {}", expiredCount + revokedCount);
  }
}
