package com.dataracy.modules.reference.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReferenceErrorStatus implements BaseErrorCode {
    NOT_FOUND_TOPIC_NAME(HttpStatus.NOT_FOUND, "REFERENCE-001", "해당하는 토픽명이 없습니다. 올바른 값을 입력해주세요."),
    NOT_FOUND_AUTHOR_LEVEL(HttpStatus.NOT_FOUND, "REFERENCE-002", "해당 작성자 유형이 존재하지 않습니다."),
    NOT_FOUND_OCCUPATION(HttpStatus.NOT_FOUND, "REFERENCE-003", "해당 직업이 존재하지 않습니다."),
    NOT_FOUND_VISIT_SOURCE(HttpStatus.NOT_FOUND, "REFERENCE-004", "해당 방문 경로가 존재하지 않습니다."),
    NOT_FOUND_ANALYSIS_PURPOSE(HttpStatus.NOT_FOUND, "REFERENCE-005", "해당 분석 목적이 존재하지 않습니다."),
    NOT_FOUND_DATA_SOURCE(HttpStatus.NOT_FOUND, "REFERENCE-006", "해당 데이터 출처가 존재하지 않습니다."),
    NOT_FOUND_DATA_TYPE(HttpStatus.NOT_FOUND, "REFERENCE-007", "해당 데이터 유형이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
