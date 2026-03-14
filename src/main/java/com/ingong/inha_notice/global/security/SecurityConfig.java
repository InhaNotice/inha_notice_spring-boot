/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-14
 */

package com.ingong.inha_notice.global.security;

import com.ingong.inha_notice.domain.auth.infra.jwt.JwtTokenProvider;
import com.ingong.inha_notice.global.security.auth.PasswordSecurityProperties;
import com.ingong.inha_notice.global.security.auth.PepperedPasswordEncoder;
import com.ingong.inha_notice.global.security.handler.ApiResponseAccessDeniedHandler;
import com.ingong.inha_notice.global.security.handler.ApiResponseAuthenticationEntryPoint;
import com.ingong.inha_notice.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final ApiResponseAuthenticationEntryPoint customAuthenticationEntryPoint; // 주입
  private final ApiResponseAccessDeniedHandler customAccessDeniedHandler;

  private static final String[] SWAGGER_URLS = {
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/swagger-ui.html",
  };
  private static final String[] AUTH_URLS = {
      "/api/v1/auth/local/join",
      "/api/v1/auth/local/login",
      "/api/v1/auth/local/refresh",
  };

  @Bean
  public PasswordEncoder passwordEncoder(PasswordSecurityProperties passwordSecurityProperties) {
    PasswordEncoder bcrypt = new BCryptPasswordEncoder(
        passwordSecurityProperties.getBcryptStrength());
    return new PepperedPasswordEncoder(bcrypt, passwordSecurityProperties.getPepper());
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(customAuthenticationEntryPoint) // 401 에러 핸들링
            .accessDeniedHandler(customAccessDeniedHandler)           // 403 에러 핸들링
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers(AUTH_URLS).permitAll()
            .requestMatchers(SWAGGER_URLS).permitAll()
            .anyRequest().authenticated()
        )

        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}