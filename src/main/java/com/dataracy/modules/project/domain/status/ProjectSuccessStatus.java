package com.dataracy.modules.project.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectSuccessStatus implements BaseSuccessCode {

    CREATED_PROJECT(HttpStatus.CREATED, "201", "프로젝트 업로드에 성공했습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
