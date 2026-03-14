/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.entity;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false, length = 100)
  private String deviceId;

  @Column(nullable = false, unique = true, length = 64)
  private String tokenHash;

  @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  private Boolean isRevoked = false;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime revokedAt;

  @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime lastUsedAt;

  @Builder
  public RefreshToken(User user, String deviceId, String tokenHash, LocalDateTime expiresAt) {
    this.user = user;
    this.deviceId = deviceId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.isRevoked = false;
  }

  public void revoke() {
    this.isRevoked = true;
    this.revokedAt = LocalDateTime.now();
  }

  public void updateLastUsedAt() {
    this.lastUsedAt = LocalDateTime.now();
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(this.expiresAt);
  }

  public boolean isValid() {
    return !isRevoked && !isExpired();
  }
}
