/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.global.security.handler;

import com.ingong.inha_notice.global.error.GlobalErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiResponseAccessDeniedHandler implements AccessDeniedHandler {

  private final SecurityErrorResponseWriter errorResponseWriter;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException ex
  ) throws IOException {

    log.warn("[Security] Access denied. method={} uri={}", request.getMethod(),
        request.getRequestURI());
    log.debug("[Security] AccessDeniedException detail", ex);

    errorResponseWriter.write(response, GlobalErrorStatus.ACCESS_DENIED);
  }
}




