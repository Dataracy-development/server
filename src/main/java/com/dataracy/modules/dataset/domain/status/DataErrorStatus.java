package com.dataracy.modules.dataset.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DataErrorStatus implements BaseErrorCode {
    FAIL_UPLOAD_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "DATA-001", "데이터셋 업로드에 실패했습니다."),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "DATA-002", "해당 데이터셋 리소스가 존재하지 않습니다."),
    BAD_REQUEST_DATE(HttpStatus.BAD_REQUEST, "DATA-003", "데이터셋 수집 시작일은 종료일보다 이전이어야 합니다."),
    FAIL_SIMILAR_SEARCH_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "DATA-005", "유사 데이터셋 검색 실패"),
    INVALID_DATA_SORT_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "DATA-006", "데이터셋 정렬은 LATEST, OLDEST, DOWNLOAD 만 가능합니다."),
    FAIL_REAL_TIME_SEARCH_DATASET(HttpStatus.INTERNAL_SERVER_ERROR, "DATA-007", "데이터셋 자동완성 검색에 실패했습니다."),
    NOT_MATCH_CREATOR(HttpStatus.FORBIDDEN, "DATA-008", "작성자만 수정 및 삭제, 복원이 가능합니다."),
    FAIL_GET_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "DATA-009", "DataUser을 생성하기 위한 유저 정보가 주입되지 않았습니다."),
    INVALID_FILE_URL(HttpStatus.BAD_REQUEST, "DATA-010", "유효하지 않은 파일 url입니다."),
    DOWNLOAD_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DATA-011", "Pre-signed URL 생성 실패했습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
