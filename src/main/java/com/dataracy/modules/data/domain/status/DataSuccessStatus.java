package com.dataracy.modules.data.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DataSuccessStatus implements BaseSuccessCode {

    CREATED_DATASET(HttpStatus.CREATED, "201", "제출이 완료되었습니다"),
    FIND_SIMILAR_DATASETS(HttpStatus.OK, "200", "유사 데이터셋 조회가 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
