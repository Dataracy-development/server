package com.dataracy.modules.topic.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TopicErrorStatus implements BaseErrorCode {

    NOT_FOUND_TOPIC_NAME(HttpStatus.BAD_REQUEST, "TOPIC-001", "해당하는 도메인 토픽명이 없습니다. 올바른 값을 입력해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
