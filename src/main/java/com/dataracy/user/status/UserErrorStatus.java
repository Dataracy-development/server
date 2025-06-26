package com.dataracy.user.status;

import com.dataracy.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {

    BAD_REQUEST_ROLE_STATUS_TYPE(HttpStatus.UNAUTHORIZED, "AUTH-001", "헤더에 어세스토큰이 누락되었습니다."),
    BAD_REQUEST_PROVIDER_STATUS_TYPE(HttpStatus.UNAUTHORIZED, "AUTH-002", "쿠키에 리프레시토큰이 누락되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
