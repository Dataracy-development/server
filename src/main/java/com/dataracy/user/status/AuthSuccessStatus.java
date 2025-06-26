package com.dataracy.user.status;

import com.dataracy.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessStatus implements BaseSuccessCode {

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
