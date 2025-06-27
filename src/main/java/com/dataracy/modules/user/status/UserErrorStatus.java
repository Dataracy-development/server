package com.dataracy.modules.user.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {

    BAD_REQUEST_ROLE_STATUS_TYPE(HttpStatus.UNAUTHORIZED, "USER-001", "헤더에 어세스토큰이 누락되었습니다."),
    BAD_REQUEST_PROVIDER_STATUS_TYPE(HttpStatus.UNAUTHORIZED, "USER-002", "쿠키에 리프레시토큰이 누락되었습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER-003", "해당하는 유저가 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
