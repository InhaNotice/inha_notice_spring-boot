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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingong.inha_notice.global.response.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.response.status.ErrorStatus;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponseWriter {

  private final ObjectMapper objectMapper;

  public void write(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
    response.setStatus(errorStatus.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    ApiResponseDTO<Void> body = ApiResponseDTO.fail(errorStatus);
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
