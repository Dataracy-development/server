package com.dataracy.modules.common.dto;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 성공 응답
 * @param <T> 데이터 타입 T
 */
@Getter
@Builder
public class SuccessResponse<T> {

    private int httpStatus;
    private String code;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> of(BaseSuccessCode successCode, T data) {
        return SuccessResponse.<T>builder()
                .httpStatus(successCode.getHttpStatus().value())
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    public static SuccessResponse<Void> of(BaseSuccessCode successCode) {
        return SuccessResponse.<Void>builder()
                .httpStatus(successCode.getHttpStatus().value())
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(null)
                .build();
    }
}
