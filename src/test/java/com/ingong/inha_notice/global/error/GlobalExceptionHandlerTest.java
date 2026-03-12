/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-03-12
 */

package com.ingong.inha_notice.global.error;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.ingong.inha_notice.domain.auth.status.AuthErrorStatus;
import com.ingong.inha_notice.global.api.dto.ApiResponseDTO;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @Mock
  private BindingResult bindingResult;

  @Nested
  @DisplayName("handleBusinessException 메서드는")
  class HandleBusinessExceptionTest {

    @Test
    void BusinessException을_처리하여_ApiResponse를_반환한다() {
      BusinessException exception = new BusinessException(AuthErrorStatus.EMAIL_ALREADY_EXISTS);

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleBusinessException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          AuthErrorStatus.EMAIL_ALREADY_EXISTS.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleHttpMessageNotReadableException 메서드는")
  class HandleHttpMessageNotReadableExceptionTest {

    @Test
    void HttpMessageNotReadableException을_처리한다() {
      HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
          "Invalid JSON", null);

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          GlobalErrorStatus.INVALID_INPUT_VALUE.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleIllegalArgumentException 메서드는")
  class HandleIllegalArgumentExceptionTest {

    @Test
    void IllegalArgumentException을_처리한다() {
      IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleIllegalArgumentException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          GlobalErrorStatus.INVALID_INPUT_VALUE.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleHttpRequestMethodNotSupportedException 메서드는")
  class HandleHttpRequestMethodNotSupportedExceptionTest {

    @Test
    void HttpRequestMethodNotSupportedException을_처리한다() {
      HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException(
          "POST");

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleHttpRequestMethodNotSupportedException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          GlobalErrorStatus.INVALID_METHOD_TYPE.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleHttpMediaTypeNotSupportedException 메서드는")
  class HandleHttpMediaTypeNotSupportedExceptionTest {

    @Test
    void HttpMediaTypeNotSupportedException을_처리한다() {
      HttpMediaTypeNotSupportedException exception = new HttpMediaTypeNotSupportedException(
          "application/xml not supported");

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleHttpMediaTypeNotSupportedException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          GlobalErrorStatus.UNSUPPORTED_MEDIA_TYPE.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleValidationException 메서드는")
  class HandleValidationExceptionTest {

    @Test
    void MethodArgumentNotValidException을_처리한다() {
      FieldError fieldError = new FieldError("user", "email", "이메일 형식이 올바르지 않습니다");
      given(bindingResult.getFieldErrors()).willReturn(Collections.singletonList(fieldError));

      MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null,
          bindingResult);

      ResponseEntity<ApiResponseDTO<Object>> response = globalExceptionHandler.handleValidationException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          GlobalErrorStatus.INVALID_INPUT_VALUE.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleNoSourceFound 메서드는")
  class HandleNoSourceFoundTest {

    @Test
    void NoResourceFoundException을_처리한다() {
      NoResourceFoundException exception = new NoResourceFoundException(
          org.springframework.http.HttpMethod.GET, "/test", "headers");

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleNoSourceFound(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(
          GlobalErrorStatus.RESOURCE_NOT_FOUND.getHttpStatus());
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }

  @Nested
  @DisplayName("handleException 메서드는")
  class HandleExceptionTest {

    @Test
    void 일반_Exception을_처리한다() {
      Exception exception = new Exception("Unexpected error");

      ResponseEntity<ApiResponseDTO<Void>> response = globalExceptionHandler.handleException(
          exception);

      assertThat(response).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().success()).isFalse();
    }
  }
}
