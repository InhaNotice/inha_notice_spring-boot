/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-16
 */

package com.ingong.inha_notice.global.api.status.common;

import org.springframework.http.HttpStatus;

public interface ResponseStatus {

  HttpStatus getHttpStatus();

  String getCode();

  String getMessage();
}
