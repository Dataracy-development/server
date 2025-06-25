package com.dataracy.common.dto;

import com.dataracy.common.status.BaseErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String code;
    private String message;

    public static ErrorResponse of(BaseErrorCode errorCode) {
        return ErrorResponse.builder()
                .httpStatus(errorCode.getHttpStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ErrorResponse of(BaseErrorCode errorCode, String customMessage) {
        return ErrorResponse.builder()
                .httpStatus(errorCode.getHttpStatus())
                .code(errorCode.getCode())
                .message(customMessage)
                .build();
    }
}
