package com.dataracy.modules.common.dto;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 에러 응답
 */
@Getter
@Builder
public class ErrorResponse {

    private int httpStatus;
    private String code;
    private String message;

    private static ErrorResponse buildResponse(BaseErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .httpStatus(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(message)
                .build();
    }

    public static ErrorResponse of(BaseErrorCode errorCode) {
        return buildResponse(errorCode, errorCode.getMessage());
    }

    public static ErrorResponse of(BaseErrorCode errorCode, String customMessage) {
        return buildResponse(errorCode, customMessage);
    }
}
