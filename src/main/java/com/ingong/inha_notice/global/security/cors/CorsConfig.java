/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.global.security.cors;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  private static final List<String> ALLOWED_METHODS = List.of(
      "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
  );

  private static final List<String> ALLOWED_HEADERS = List.of(
      "Authorization", "Content-Type"
  );

  @Value("${management.endpoints.web.cors.allowed-origins}")
  private List<String> allowedOrigins;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(ALLOWED_METHODS);
    config.setAllowedHeaders(ALLOWED_HEADERS);
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
