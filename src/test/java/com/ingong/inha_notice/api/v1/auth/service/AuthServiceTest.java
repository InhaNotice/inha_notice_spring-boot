/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.api.v1.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ingong.inha_notice.api.v1.auth.dto.local.request.JoinRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.local.request.LoginRequestDTO;
import com.ingong.inha_notice.api.v1.auth.dto.local.response.JoinResponseDTO;
import com.ingong.inha_notice.api.v1.auth.dto.local.response.LoginResponseDTO;
import com.ingong.inha_notice.domain.auth.dto.TokenResponseDTO;
import com.ingong.inha_notice.domain.auth.infra.jwt.JwtTokenProvider;
import com.ingong.inha_notice.domain.auth.service.AuthService;
import com.ingong.inha_notice.domain.auth.status.AuthErrorStatus;
import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import com.ingong.inha_notice.domain.user.repository.UserRepository;
import com.ingong.inha_notice.global.error.BusinessException;
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
      then(jwtTokenProvider).shouldHaveNoInteractions();
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
      then(jwtTokenProvider).shouldHaveNoInteractions();
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
      TokenResponseDTO expectedToken =
          TokenResponseDTO.of("accessToken", "refreshToken", 3_600_000L);

      given(userRepository.existsByEmail(validRequest.email())).willReturn(false);
      given(passwordEncoder.encode(validRequest.password())).willReturn(encodedPassword);
      given(userRepository.save(any(User.class))).willReturn(savedUser);
      given(jwtTokenProvider.createTokens(anyString())).willReturn(expectedToken);

      JoinResponseDTO result = authService.join(validRequest);

      assertThat(result.tokenResponseDTO()).isEqualTo(expectedToken);
      assertThat(result.email()).isEqualTo(validRequest.email());
      assertThat(result.isPrivacyAgreed()).isTrue();

      then(userRepository).should().existsByEmail(validRequest.email());
      then(passwordEncoder).should().encode(validRequest.password());
      then(userRepository).should().save(any(User.class));
      then(jwtTokenProvider).should().createTokens(savedUser.getPublicId());
    }
  }

  @Nested
  @DisplayName("login 메서드는")
  class LoginTest {

    private final LoginRequestDTO validRequest = new LoginRequestDTO("test@example.com",
        "Password123!");

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

      given(userRepository.findByEmail(validRequest.email())).willReturn(Optional.of(validUser));
      given(passwordEncoder.matches(validRequest.password(), validUser.getPassword())).willReturn(
          true);
      given(jwtTokenProvider.createTokens(anyString())).willReturn(expectedToken);
      LoginResponseDTO result = authService.login(validRequest);

      assertThat(result.tokenResponseDTO()).isEqualTo(expectedToken);
      assertThat(result.email()).isEqualTo(validRequest.email());

      then(userRepository).should().findByEmail(validRequest.email());
      then(passwordEncoder).should().matches(validRequest.password(), validUser.getPassword());
      then(jwtTokenProvider).should().createTokens(validUser.getPublicId());
    }
  }
}
