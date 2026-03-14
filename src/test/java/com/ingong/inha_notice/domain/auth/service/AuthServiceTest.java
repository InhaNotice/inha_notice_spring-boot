/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.domain.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ingong.inha_notice.api.v1.auth.dto.request.jwt.RefreshTokenRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.request.local.LogoutRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.response.local.LoginResponseDTO;
import com.ingong.inha_notice.domain.auth.entity.RefreshToken;
import com.ingong.inha_notice.domain.auth.infra.jwt.JwtProperties;
import com.ingong.inha_notice.domain.auth.infra.jwt.JwtTokenProvider;
import com.ingong.inha_notice.domain.auth.infra.redis.RedisRefreshTokenStore;
import com.ingong.inha_notice.domain.auth.repository.RefreshTokenRepository;
import com.ingong.inha_notice.domain.auth.status.AuthErrorStatus;
import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import com.ingong.inha_notice.global.error.BusinessException;
import com.ingong.inha_notice.global.security.auth.AuthenticatedUser;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @InjectMocks
  private AuthService authService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private RedisRefreshTokenStore redisRefreshTokenStore;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private JwtProperties jwtProperties;

  @Nested
  @DisplayName("join 메서드는")
  class JoinTest {

    private final JoinRequestDTO validRequest = new JoinRequestDTO("test@example.com",
        "Password123!", true);

    @Test
    void 존재하는_이메일이면_예외를_던진다() {
      given(userRepository.existsByEmail(validRequest.email())).willReturn(true);

      assertThatThrownBy(() -> authService.join(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus()).isEqualTo(
              AuthErrorStatus.EMAIL_ALREADY_EXISTS));

      then(userRepository).should().existsByEmail(validRequest.email());
      then(userRepository).shouldHaveNoMoreInteractions();
      then(passwordEncoder).shouldHaveNoInteractions();
    }

    @Test
    void 중복키가_발생하면_예외를_던진다() {
      given(userRepository.existsByEmail(validRequest.email())).willReturn(false);
      given(passwordEncoder.encode(validRequest.password())).willReturn("encodedPassword");
      given(userRepository.save(any(User.class))).willThrow(
          new DataIntegrityViolationException("duplicate record"));

      assertThatThrownBy(() -> authService.join(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex ->
              assertThat(((BusinessException) ex).getErrorStatus())
                  .isEqualTo(
                      AuthErrorStatus.EMAIL_ALREADY_EXISTS));

      then(userRepository).should().existsByEmail(validRequest.email());
      then(passwordEncoder).should().encode(validRequest.password());
      then(userRepository).should().save(any(User.class));
    }

    @Test
    void 유효한_회원가입이_진행된다() {
      String encodedPassword = "encodedPassword";
      User savedUser = User.builder()
          .email(validRequest.email())
          .password(encodedPassword)
          .isPrivacyAgreed(validRequest.isPrivacyAgreed())
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();

      given(userRepository.existsByEmail(validRequest.email())).willReturn(false);
      given(passwordEncoder.encode(validRequest.password())).willReturn(encodedPassword);
      given(userRepository.save(any(User.class))).willReturn(savedUser);

      JoinResponseDTO result = authService.join(validRequest);

      assertThat(result.email()).isEqualTo(validRequest.email());
      assertThat(result.isPrivacyAgreed()).isTrue();

      then(userRepository).should().existsByEmail(validRequest.email());
      then(passwordEncoder).should().encode(validRequest.password());
      then(userRepository).should().save(any(User.class));
    }
  }

  @Nested
  @DisplayName("login 메서드는")
  class LoginTest {

    private final LoginRequestDTO validRequest = new LoginRequestDTO("test@example.com",
        "Password123!", "device-123");

    @Test
    void 존재하지않는_이메일이면_예외를_던진다() {
      given(userRepository.findByEmail(validRequest.email())).willReturn(Optional.empty());

      assertThatThrownBy(() -> authService.login(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus()).isEqualTo(
              AuthErrorStatus.LOGIN_FAILED));

      then(userRepository).should().findByEmail(validRequest.email());
      then(userRepository).shouldHaveNoMoreInteractions();
      then(passwordEncoder).shouldHaveNoMoreInteractions();
      then(jwtTokenProvider).shouldHaveNoInteractions();
      then(refreshTokenRepository).shouldHaveNoInteractions();
      then(redisRefreshTokenStore).shouldHaveNoInteractions();
    }

    @Test
    void 비밀번호가_일치하지않으면_예외를_던진다() {
      User validUser = User.builder()
          .email(validRequest.email())
          .password("NotPassword123!")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();

      given(userRepository.findByEmail(validRequest.email())).willReturn(Optional.of(validUser));
      given(passwordEncoder.matches(validRequest.password(), validUser.getPassword())).willReturn(
          false);

      assertThatThrownBy(
          () -> authService.login(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus()).isEqualTo(
              AuthErrorStatus.LOGIN_FAILED));

      then(userRepository).should().findByEmail(validRequest.email());
      then(passwordEncoder).should().matches(validRequest.password(), validUser.getPassword());
      then(jwtTokenProvider).shouldHaveNoMoreInteractions();
      then(refreshTokenRepository).shouldHaveNoInteractions();
      then(redisRefreshTokenStore).shouldHaveNoInteractions();
    }

    @Test
    void 유저의상태가_ACTIVE가아니면_예외를_던진다() {
      User validUser = User.builder()
          .email(validRequest.email())
          .password("Password123!")
          .isPrivacyAgreed(true)
          .status(UserStatus.BANNED)
          .role(UserRole.USER)
          .build();

      given(userRepository.findByEmail(validRequest.email())).willReturn(Optional.of(validUser));
      given(passwordEncoder.matches(validRequest.password(), validUser.getPassword())).willReturn(
          true);

      assertThatThrownBy(
          () -> authService.login(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus()).isEqualTo(
              AuthErrorStatus.LOGIN_FAILED));

      then(userRepository).should().findByEmail(validRequest.email());
      then(passwordEncoder).should().matches(validRequest.password(), validUser.getPassword());
      then(jwtTokenProvider).shouldHaveNoMoreInteractions();
      then(refreshTokenRepository).shouldHaveNoInteractions();
      then(redisRefreshTokenStore).shouldHaveNoInteractions();
    }

    @Test
    void 유효한_로그인이_진행된다() {
      User validUser = User.builder()
          .email(validRequest.email())
          .password("Password123!")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();
      TokenResponseDTO expectedToken =
          TokenResponseDTO.of("accessToken", "refreshToken", 3_600_000L);
      JwtProperties.TokenInfo refreshTokenConfig = new JwtProperties.TokenInfo();
      refreshTokenConfig.setExpiration(3_600_000L);

      given(userRepository.findByEmail(validRequest.email())).willReturn(Optional.of(validUser));
      given(passwordEncoder.matches(validRequest.password(), validUser.getPassword())).willReturn(
          true);
      willDoNothing().given(refreshTokenRepository)
          .deleteByUserPublicIdAndDeviceId(anyString(), anyString());
      willDoNothing().given(redisRefreshTokenStore).delete(anyString(), anyString());
      given(jwtTokenProvider.issueTokenPair(anyString())).willReturn(expectedToken);
      given(jwtProperties.getRefreshToken()).willReturn(refreshTokenConfig);
      given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(
          invocation -> invocation.getArgument(0));
      willDoNothing().given(redisRefreshTokenStore)
          .save(anyString(), anyString(), anyString(), any(Duration.class));

      LoginResponseDTO result = authService.login(validRequest);

      assertThat(result.tokens()).isEqualTo(expectedToken);
      assertThat(result.user().email()).isEqualTo(validRequest.email());
      assertThat(result.user().status()).isEqualTo(UserStatus.ACTIVE);
      assertThat(result.user().role()).isEqualTo(UserRole.USER);

      then(userRepository).should().findByEmail(validRequest.email());
      then(passwordEncoder).should().matches(validRequest.password(), validUser.getPassword());
      then(refreshTokenRepository).should()
          .deleteByUserPublicIdAndDeviceId(validUser.getPublicId(), validRequest.deviceId());
      then(redisRefreshTokenStore).should()
          .delete(validUser.getPublicId(), validRequest.deviceId());
      then(jwtTokenProvider).should().issueTokenPair(validUser.getPublicId());
      then(refreshTokenRepository).should().save(any(RefreshToken.class));
      then(redisRefreshTokenStore).should()
          .save(eq(validUser.getPublicId()), eq(validRequest.deviceId()), anyString(),
              any(Duration.class));
    }
  }

  @Nested
  @DisplayName("refresh 메서드는")
  class RefreshTest {

    private final String validRefreshToken = "validRefreshToken";
    private final String deviceId = "device-123";
    private final String publicId = "user-public-id";
    private final RefreshTokenRequestDTO validRequest = new RefreshTokenRequestDTO(
        validRefreshToken, deviceId);

    @Test
    void 유효하지_않은_리프레시_토큰이면_예외를_던진다() {
      given(jwtTokenProvider.extractPublicIdFromRefresh(validRefreshToken))
          .willThrow(new RuntimeException("Invalid token"));

      assertThatThrownBy(() -> authService.refresh(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus())
              .isEqualTo(AuthErrorStatus.INVALID_REFRESH_TOKEN));

      then(jwtTokenProvider).should().extractPublicIdFromRefresh(validRefreshToken);
      then(redisRefreshTokenStore).shouldHaveNoInteractions();
      then(refreshTokenRepository).shouldHaveNoInteractions();
    }

    @Test
    void Redis와_DB에_모두_저장된_토큰이_없으면_예외를_던진다() {
      given(jwtTokenProvider.extractPublicIdFromRefresh(validRefreshToken)).willReturn(publicId);
      given(redisRefreshTokenStore.find(publicId, deviceId)).willReturn(Optional.empty());
      given(refreshTokenRepository.findValidTokenByUserPublicIdAndDeviceId(
          eq(publicId), eq(deviceId), any())).willReturn(Optional.empty());

      assertThatThrownBy(() -> authService.refresh(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus())
              .isEqualTo(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND));

      then(jwtTokenProvider).should().extractPublicIdFromRefresh(validRefreshToken);
      then(redisRefreshTokenStore).should().find(publicId, deviceId);
      then(refreshTokenRepository).should()
          .findValidTokenByUserPublicIdAndDeviceId(eq(publicId), eq(deviceId), any());
    }

    @Test
    void 토큰_해시가_일치하지_않으면_예외를_던진다() {
      String storedHash = "wrongHash123";

      given(jwtTokenProvider.extractPublicIdFromRefresh(validRefreshToken)).willReturn(publicId);
      given(redisRefreshTokenStore.find(publicId, deviceId)).willReturn(Optional.of(storedHash));

      assertThatThrownBy(() -> authService.refresh(validRequest))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus())
              .isEqualTo(AuthErrorStatus.INVALID_REFRESH_TOKEN));

      then(jwtTokenProvider).should().extractPublicIdFromRefresh(validRefreshToken);
      then(redisRefreshTokenStore).should().find(publicId, deviceId);
      then(refreshTokenRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 유효한_리프레시_토큰으로_액세스_토큰을_재발급한다() {
      // SHA-256 hash of "validRefreshToken"
      String expectedHash =
          "37358d092508668e00565187c0d9870f46d5c1ae843e6f8afe2b1182b99d2541";
      TokenResponseDTO expectedToken = TokenResponseDTO.of("newAccessToken",
          validRefreshToken, 3_600_000L);

      User mockUser = User.builder()
          .email("test@example.com")
          .password("encodedPassword")
          .isPrivacyAgreed(true)
          .status(UserStatus.ACTIVE)
          .role(UserRole.USER)
          .build();

      RefreshToken mockRefreshToken = RefreshToken.builder()
          .user(mockUser)
          .deviceId(deviceId)
          .tokenHash(expectedHash)
          .expiresAt(java.time.LocalDateTime.now().plusDays(7))
          .build();

      given(jwtTokenProvider.extractPublicIdFromRefresh(validRefreshToken)).willReturn(publicId);
      given(redisRefreshTokenStore.find(publicId, deviceId)).willReturn(Optional.of(expectedHash));
      given(refreshTokenRepository.findValidTokenByUserPublicIdAndDeviceId(
          eq(publicId), eq(deviceId), any())).willReturn(Optional.of(mockRefreshToken));
      given(jwtTokenProvider.reissueAccessToken(validRefreshToken))
          .willReturn(expectedToken);

      TokenResponseDTO result = authService.refresh(validRequest);

      assertThat(result.accessToken()).isEqualTo("newAccessToken");
      assertThat(result.refreshToken()).isEqualTo(validRefreshToken);

      then(jwtTokenProvider).should().extractPublicIdFromRefresh(validRefreshToken);
      then(redisRefreshTokenStore).should().find(publicId, deviceId);
      then(refreshTokenRepository).should()
          .findValidTokenByUserPublicIdAndDeviceId(eq(publicId), eq(deviceId), any());
      then(jwtTokenProvider).should().reissueAccessToken(validRefreshToken);
    }
  }

  @Nested
  @DisplayName("logout 메서드는")
  class LogoutTest {

    private final String publicId = "user-public-id";
    private final String deviceId = "device-123";
    private final AuthenticatedUser authenticatedUser = new AuthenticatedUser(
        publicId,
        "encodedPassword",
        UserStatus.ACTIVE,
        UserRole.USER,
        java.util.Collections.singletonList(
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
    );

    @Test
    void 인증되지_않은_사용자면_예외를_던진다() {
      LogoutRequestDTO request = new LogoutRequestDTO(false, deviceId);

      assertThatThrownBy(() -> authService.logout(null, request))
          .isInstanceOf(BusinessException.class)
          .satisfies(ex -> assertThat(((BusinessException) ex).getErrorStatus())
              .isEqualTo(AuthErrorStatus.ACCESS_DENIED));

      then(refreshTokenRepository).shouldHaveNoInteractions();
      then(redisRefreshTokenStore).shouldHaveNoInteractions();
    }

    @Test
    void 단일_디바이스에서_로그아웃한다() {
      LogoutRequestDTO request = new LogoutRequestDTO(false, deviceId);
      willDoNothing().given(refreshTokenRepository)
          .deleteByUserPublicIdAndDeviceId(publicId, deviceId);
      willDoNothing().given(redisRefreshTokenStore).delete(publicId, deviceId);

      authService.logout(authenticatedUser, request);

      then(refreshTokenRepository).should().deleteByUserPublicIdAndDeviceId(publicId, deviceId);
      then(redisRefreshTokenStore).should().delete(publicId, deviceId);
      then(refreshTokenRepository).shouldHaveNoMoreInteractions();
      then(redisRefreshTokenStore).shouldHaveNoMoreInteractions();
    }

    @Test
    void 모든_디바이스에서_로그아웃한다() {
      LogoutRequestDTO request = new LogoutRequestDTO(true, deviceId);
      willDoNothing().given(refreshTokenRepository).deleteAllByUserPublicId(publicId);
      willDoNothing().given(redisRefreshTokenStore).deleteAllByUserPublicId(publicId);

      authService.logout(authenticatedUser, request);

      then(refreshTokenRepository).should().deleteAllByUserPublicId(publicId);
      then(redisRefreshTokenStore).should().deleteAllByUserPublicId(publicId);
      then(refreshTokenRepository).shouldHaveNoMoreInteractions();
      then(redisRefreshTokenStore).shouldHaveNoMoreInteractions();
    }
  }
}
