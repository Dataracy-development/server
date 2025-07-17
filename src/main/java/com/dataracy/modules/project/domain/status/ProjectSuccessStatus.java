package com.dataracy.modules.project.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectSuccessStatus implements BaseSuccessCode {

    CREATED_PROJECT(HttpStatus.CREATED, "201", "제출이 완료되었습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
