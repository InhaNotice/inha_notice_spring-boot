/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-18
 */

package com.ingong.inha_notice.global.security.handler;

import com.ingong.inha_notice.global.error.GlobalErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiResponseAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final SecurityErrorResponseWriter errorResponseWriter;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException ex
  ) throws IOException {

    log.warn("[Security] Authentication failed. method={} uri={}",
        request.getMethod(), request.getRequestURI());
    log.debug("[Security] AuthenticationException detail", ex);
    
    errorResponseWriter.write(response, GlobalErrorStatus.UNAUTHORIZED_ACCESS);
  }
}
