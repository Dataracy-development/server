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
    FIND_REAL_TIME_DATASETS(HttpStatus.OK, "200", "데이터셋 자동완성을 위한 데이터셋 목록을 조회한다."),
    COUNT_DATASETS_GROUP_BY_TOPIC(HttpStatus.OK, "200", "토픽 카테고리별 데이터셋 개수를 조회한다."),
    GET_CONNECTED_DATASETS_ASSOCIATED_PROJECT(HttpStatus.OK, "200", "프로젝트와 연결된 데이터셋 리스트 조회가 완료되었습니다."),
    MODIFY_DATASET(HttpStatus.OK, "200", "수정이 완료되었습니다"),
    DELETE_DATASET(HttpStatus.OK, "200", "데이터셋 삭제가 완료되었습니다."),
    RESTORE_DATASET(HttpStatus.OK, "200", "데이터셋 복원에 완료되었습니다."),
    DOWNLOAD_DATASET(HttpStatus.OK, "200", "유효기간이 있는 데이터셋 다운로드 URL을 반환된다.."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
