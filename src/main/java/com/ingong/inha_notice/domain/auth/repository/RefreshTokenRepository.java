/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.repository;

import com.ingong.inha_notice.domain.auth.entity.RefreshToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  // 특정 사용자의 특정 디바이스에 대한 유효한 Refresh Token 조회
  @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.publicId = :publicId "
      + "AND rt.deviceId = :deviceId "
      + "AND rt.isRevoked = false "
      + "AND rt.expiresAt > :now")
  Optional<RefreshToken> findValidTokenByUserPublicIdAndDeviceId(
      @Param("publicId") String publicId,
      @Param("deviceId") String deviceId,
      @Param("now") LocalDateTime now
  );

  // 특정 사용자의 모든 Refresh Token 조회
  @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.publicId = :publicId")
  List<RefreshToken> findAllByUserPublicId(@Param("publicId") String publicId);

  // 특정 사용자의 특정 디바이스 토큰 삭제
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.user.publicId = :publicId AND rt.deviceId = :deviceId")
  void deleteByUserPublicIdAndDeviceId(
      @Param("publicId") String publicId,
      @Param("deviceId") String deviceId
  );

  // 특정 사용자의 모든 토큰 삭제
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.user.publicId = :publicId")
  void deleteAllByUserPublicId(@Param("publicId") String publicId);

  //  만료된 토큰 삭제
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
  int deleteExpiredTokens(@Param("now") LocalDateTime now);

  // 취소된 토큰 삭제
  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.isRevoked = true")
  int deleteRevokedTokens();

  // 특정 사용자의 활성 세션 수 조회
  @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user.publicId = :publicId "
      + "AND rt.isRevoked = false "
      + "AND rt.expiresAt > :now")
  long countActiveSessionsByUserPublicId(
      @Param("publicId") String publicId,
      @Param("now") LocalDateTime now
  );
}
