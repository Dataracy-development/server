package com.dataracy.common.dto;

import com.dataracy.common.status.BaseSuccessCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class SuccessResponse<T> {

    private HttpStatus httpStatus;
    private String code;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> of(BaseSuccessCode successCode, T data) {
        return SuccessResponse.<T>builder()
                .httpStatus(successCode.getHttpStatus())
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> SuccessResponse<T> of(BaseSuccessCode successCode) {
        return SuccessResponse.<T>builder()
                .httpStatus(successCode.getHttpStatus())
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(null)
                .build();
    }
}
