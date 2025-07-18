package com.dataracy.modules.project.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectSuccessStatus implements BaseSuccessCode {

    CREATED_PROJECT(HttpStatus.CREATED, "201", "제출이 완료되었습니다"),
    FIND_REAL_TIME_PROJECTS(HttpStatus.OK, "200", "실시간 프로젝트 리스트를 조회하였습니다."),
    FIND_SIMILAR_PROJECTS(HttpStatus.OK, "200", "유사 프로젝트 리스트를 조회하였습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
