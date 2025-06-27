package com.dataracy.modules.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonSuccessStatus implements BaseSuccessCode {

    // Global
    OK(HttpStatus.OK, "GLOBAL-200", "성공입니다."),
    CREATED(HttpStatus.CREATED, "GLOBAL-201", "생성에 성공했습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "GLOBAL-204", "성공입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
