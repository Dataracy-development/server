package com.dataracy.common.exception;

import com.dataracy.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public String getCode() {
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
