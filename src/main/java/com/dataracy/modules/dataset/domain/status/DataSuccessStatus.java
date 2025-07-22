package com.dataracy.modules.dataset.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DataSuccessStatus implements BaseSuccessCode {

    CREATED_DATASET(HttpStatus.CREATED, "201", "제출이 완료되었습니다"),
    FIND_SIMILAR_DATASETS(HttpStatus.OK, "200", "유사 데이터셋 조회가 완료되었습니다."),
    FIND_POPULAR_DATASETS(HttpStatus.OK, "200", "인기 데이터셋 조회가 완료되었습니다."),
    GET_DATA_DETAIL(HttpStatus.OK, "200", "데이터 상세 정보 조회가 완료되었습니다."),
    FIND_FILTERED_DATASETS(HttpStatus.OK, "200", "데이터셋 필터링이 완료되었습니다."),
    GET_RECENT_DATASETS(HttpStatus.OK, "200", "최신 데이터셋 목록 조회가 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
