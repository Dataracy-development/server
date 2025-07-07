package com.dataracy.modules.topic.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TopicSuccessStatus implements BaseSuccessCode {
    OK_TOTAL_TOPIC_LIST(HttpStatus.OK, "200", "도메인 리스트 조회에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
