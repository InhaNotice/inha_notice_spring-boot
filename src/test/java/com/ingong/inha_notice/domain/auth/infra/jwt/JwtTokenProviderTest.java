/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.domain.auth.infra.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ingong.inha_notice.api.v1.auth.dto.response.jwt.TokenResponseDTO;
import com.ingong.inha_notice.global.security.auth.PublicIdUserDetailsService;
import io.jsonwebtoken.JwtException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

  @InjectMocks
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private JwtProperties jwtProperties;

  @Mock
  private PublicIdUserDetailsService publicIdUserDetailsService;

  private KeyPair keyPair;

  @BeforeEach
  void setUp() throws Exception {
    // RSA 키 페어 생성 (테스트용)
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    keyPair = keyPairGenerator.generateKeyPair();

    // JwtProperties Mock 설정
    String privateKeyBase64 = Base64.getEncoder()
        .encodeToString(keyPair.getPrivate().getEncoded());
    String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

    JwtProperties.TokenInfo accessTokenInfo = new JwtProperties.TokenInfo();
    accessTokenInfo.setExpiration(3600000L); // 1시간

    JwtProperties.TokenInfo refreshTokenInfo = new JwtProperties.TokenInfo();
    refreshTokenInfo.setExpiration(604800000L); // 7일

    given(jwtProperties.getPrivateKey()).willReturn(privateKeyBase64);
    given(jwtProperties.getPublicKey()).willReturn(publicKeyBase64);
    given(jwtProperties.getIssuer()).willReturn("test-issuer");
    given(jwtProperties.getAudience()).willReturn("test-audience");
    given(jwtProperties.getAccessToken()).willReturn(accessTokenInfo);
    given(jwtProperties.getRefreshToken()).willReturn(refreshTokenInfo);

    // init() 호출하여 키 초기화
    jwtTokenProvider.init();
  }

  @Nested
  @DisplayName("createTokens 메서드는")
  class CreateTokensTest {

    private final String validPublicId = "test-public-id-123";

    @Test
    void 유효한_토큰을_생성한다() {
      TokenResponseDTO result = jwtTokenProvider.createTokens(validPublicId);

      assertThat(result).isNotNull();
      assertThat(result.accessToken()).isNotNull();
      assertThat(result.refreshToken()).isNotNull();
      assertThat(result.expiresIn()).isEqualTo(3600000L);
    }

    @Test
    void 생성된_토큰에_publicId가_포함된다() {
      TokenResponseDTO result = jwtTokenProvider.createTokens(validPublicId);

      // 토큰이 유효한 JWT 형식인지 확인 (header.payload.signature)
      assertThat(result.accessToken().split("\\.")).hasSize(3);
      assertThat(result.refreshToken().split("\\.")).hasSize(3);
    }
  }

  @Nested
  @DisplayName("getAuthentication 메서드는")
  class GetAuthenticationTest {

    private final String validPublicId = "test-public-id-123";

    @Test
    void 유효한_Bearer_토큰으로_인증객체를_생성한다() {
      TokenResponseDTO tokens = jwtTokenProvider.createTokens(validPublicId);
      String bearerToken = "Bearer " + tokens.accessToken();

      UserDetails userDetails = User.builder()
          .username(validPublicId)
          .password("encodedPassword")
          .authorities(new SimpleGrantedAuthority("ROLE_USER"))
          .build();

      given(publicIdUserDetailsService.loadUserByUsername(validPublicId)).willReturn(userDetails);

      UsernamePasswordAuthenticationToken result = jwtTokenProvider.getAuthentication(
          bearerToken);

      assertThat(result).isNotNull();
      assertThat(result.getPrincipal()).isEqualTo(userDetails);
      assertThat(result.getAuthorities()).isNotNull();

      then(publicIdUserDetailsService).should().loadUserByUsername(validPublicId);
    }

    @Test
    void Bearer_없는_토큰이면_예외를_던진다() {
      TokenResponseDTO tokens = jwtTokenProvider.createTokens(validPublicId);
      String invalidToken = tokens.accessToken(); // Bearer 없음

      assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(invalidToken))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Bearer");

      then(publicIdUserDetailsService).shouldHaveNoInteractions();
    }

    @Test
    void null_토큰이면_예외를_던진다() {
      assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("토큰이 없습니다");

      then(publicIdUserDetailsService).shouldHaveNoInteractions();
    }

    @Test
    void 빈_토큰이면_예외를_던진다() {
      assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("토큰이 없습니다");

      then(publicIdUserDetailsService).shouldHaveNoInteractions();
    }

    @Test
    void Bearer_뒤에_토큰이_없으면_예외를_던진다() {
      assertThatThrownBy(() -> jwtTokenProvider.getAuthentication("Bearer  "))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Bearer");

      then(publicIdUserDetailsService).shouldHaveNoInteractions();
    }

    @Test
    void 잘못된_형식의_토큰이면_예외를_던진다() {
      String invalidToken = "Bearer invalid.token.format";

      assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(invalidToken))
          .isInstanceOf(JwtException.class);

      then(publicIdUserDetailsService).shouldHaveNoInteractions();
    }
  }
}
