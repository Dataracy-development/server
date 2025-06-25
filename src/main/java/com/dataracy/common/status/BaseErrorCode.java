package com.dataracy.common.status;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
