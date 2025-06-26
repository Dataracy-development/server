package com.dataracy.user.status;

import com.dataracy.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorStatus implements BaseErrorCode {

    NOT_FOUND_ACCESS_TOKEN_IN_HEADER(HttpStatus.UNAUTHORIZED, "AUTH-001", "헤더에 어세스토큰이 누락되었습니다."),
    NOT_FOUND_REFRESH_TOKEN_IN_COOKIES(HttpStatus.UNAUTHORIZED, "AUTH-002", "쿠키에 리프레시토큰이 누락되었습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "유효하지 않은 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
