package com.dataracy.common.status;

import org.springframework.http.HttpStatus;

public interface BaseSuccessCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
