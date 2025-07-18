package com.dataracy.modules.data.domain.status;

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
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
