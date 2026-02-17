/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.domain.user.entity;

import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.global.entity.BaseTimeEntity;
import com.ingong.inha_notice.global.util.UlidGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, columnDefinition = "CHAR(26)")
  private String publicId;

  @Column(nullable = false, unique = true)
  private String email;

  @Column() // 소셜 로그인은 비밀번호가 없음. 로컬 가입자만 존재.
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Column(nullable = false)
  private Boolean isPrivacyAgreed;

  @Column()
  private LocalDateTime lastLoginAt;

  @Column()
  private LocalDateTime passwordChangedAt;

  @PrePersist
  private void prePersist() {
    if (this.publicId == null || this.publicId.isBlank()) {
      this.publicId = UlidGenerator.newUlid();
    }
    if (this.role == null) {
      this.role = UserRole.USER;
    }
    if (this.status == null) {
      this.status = UserStatus.ACTIVE;
    }
  }

  @Builder
  public User(String email, String password, Boolean isPrivacyAgreed, UserStatus status,
      UserRole role) {
    this.publicId = UlidGenerator.newUlid();
    this.email = email;
    this.password = password;
    this.isPrivacyAgreed = isPrivacyAgreed;
    this.status = status;
    this.role = role;
  }
}
