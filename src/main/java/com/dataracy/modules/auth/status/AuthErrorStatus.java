package com.dataracy.modules.auth.status;

import com.dataracy.modules.common.status.BaseErrorCode;
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
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "AUTH-005", "인증에 실패했습니다. 현재 로그인된 유저가 아닙니다."),
    INVALID_REGISTER_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-006", "유효하지 않은 레지스터 토큰입니다."),
    EXPIRED_REGISTER_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-007", "추가정보 입력시간이 만료되었습니다. 재회원가입을 시도해주세요."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-008", "리프레쉬 토큰이 만료되었습니다. 재로그인을 시도해주세요."),
    TOKEN_USER_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH-009", "인증된 사용자 정보와 어세스 토큰이 일치하지 않습니다."),
    NOT_SUPPORTED_SOCIAL_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "AUTH-010", "허용된 소셜 타입으로만 소셜로그인을 진행해주세요. (구글, 카카오)"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
