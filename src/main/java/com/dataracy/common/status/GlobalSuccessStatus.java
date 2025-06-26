package com.dataracy.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalSuccessStatus implements BaseSuccessCode {

    // Global
    _OK(HttpStatus.OK, "GLOBAL-200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "GLOBAL-201", "생성에 성공했습니다."),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "GLOBAL-204", "성공입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
