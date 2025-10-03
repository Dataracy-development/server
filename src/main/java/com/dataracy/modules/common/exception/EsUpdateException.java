/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.exception;

public class EsUpdateException extends RuntimeException {
  public EsUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
