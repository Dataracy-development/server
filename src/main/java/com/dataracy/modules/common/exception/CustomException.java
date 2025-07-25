package com.dataracy.modules.common.exception;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 처리 공통 응답을 위한 커스텀 Exception
 */
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
