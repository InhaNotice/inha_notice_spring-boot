/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-21
 */

package com.ingong.inha_notice.global.error;

import com.ingong.inha_notice.global.api.dto.ApiResponseDTO;
import com.ingong.inha_notice.global.api.dto.FieldErrorDetailDTO;
import com.ingong.inha_notice.global.api.status.ErrorStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Business Error
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleBusinessException(BusinessException e) {
    return fail(e, e.getErrorStatus());
  }

  // Http / Protocol
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    return fail(e, GlobalErrorStatus.INVALID_INPUT_VALUE);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgumentException(
      IllegalArgumentException e) {
    return fail(e, GlobalErrorStatus.INVALID_INPUT_VALUE);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    return fail(e, GlobalErrorStatus.INVALID_METHOD_TYPE);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException e) {
    return fail(e, GlobalErrorStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDTO<Object>> handleValidationException(
      MethodArgumentNotValidException e) {

    List<FieldErrorDetailDTO> fieldErrors = e.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldErrorDetailDTO(
            error.getField(),
            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
            error.getDefaultMessage()
        ))
        .toList();

    log.warn("[Client Error] Validation failed for fields: {}",
        fieldErrors.stream().map(FieldErrorDetailDTO::field).collect(Collectors.joining(", ")));

    return ResponseEntity
        .status(GlobalErrorStatus.INVALID_INPUT_VALUE.getHttpStatus())
        .body(ApiResponseDTO.fail(GlobalErrorStatus.INVALID_INPUT_VALUE, fieldErrors));
  }

  // Infra / Server
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleNoSourceFound(
      NoResourceFoundException e
  ) {
    return fail(e, GlobalErrorStatus.RESOURCE_NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDTO<Void>> handleException(Exception e) {
    return fail(e, GlobalErrorStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ApiResponseDTO<Void>> fail(Exception e, ErrorStatus errorStatus) {
    if (errorStatus.getHttpStatus().is5xxServerError()) {
      log.error("[Server Error] {}", errorStatus.getMessage(), e);
    } else {
      log.warn("[Client Error] {}: {}", errorStatus.getMessage(), e.getMessage()); // Warn 레벨로 분리
    }

    return ResponseEntity
        .status(errorStatus.getHttpStatus())
        .body(ApiResponseDTO.fail(errorStatus));
  }
}
