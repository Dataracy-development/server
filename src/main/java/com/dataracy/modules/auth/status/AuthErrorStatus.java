package com.dataracy.modules.auth.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorStatus implements BaseErrorCode {

    BAD_REQUEST_LOGIN(HttpStatus.BAD_REQUEST, "AUTH-001", "이메일 또는 비밀번호가 올바르지 않습니다."),

    NOT_FOUND_ACCESS_TOKEN_IN_HEADER(HttpStatus.UNAUTHORIZED, "AUTH-001", "헤더에 어세스토큰이 누락되었습니다."),
    NOT_FOUND_REFRESH_TOKEN_IN_COOKIES(HttpStatus.UNAUTHORIZED, "AUTH-002", "쿠키에 리프레시토큰이 누락되었습니다."),

    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "AUTH-003", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "유효하지 않은 토큰입니다."),
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "AUTH-005", "인증에 실패했습니다. 현재 로그인된 유저가 아닙니다."),

    INVALID_REGISTER_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-006", "유효하지 않은 레지스터 토큰입니다."),
    EXPIRED_REGISTER_TOKEN(HttpStatus.FORBIDDEN, "AUTH-007", "추가정보 입력시간이 만료되었습니다. 재회원가입을 시도해주세요."),

    EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "AUTH-008", "리프레쉬 토큰이 만료되었습니다. 재로그인을 시도해주세요."),
    TOKEN_USER_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH-009", "인증된 사용자 정보와 어세스 토큰이 일치하지 않습니다."),

    NOT_SUPPORTED_SOCIAL_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "AUTH-010", "허용된 소셜 타입으로만 소셜로그인을 진행해주세요. (구글, 카카오)"),
    FAILED_SAVE_REFRESH_TOKEN_IN_REDIS(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-011", "리프레시 토큰의 Redis 저장에 실패했습니다."),
    REFRESH_TOKEN_NOT_FOUND_IN_REDIS(HttpStatus.NOT_FOUND, "AUTH-012", "클라이언트 쿠키로부터 제공받은 리프레시 토큰의 유저가 레디스에 존재하지 않습니다."),
    REFRESH_TOKEN_USER_MISMATCH_IN_REDIS(HttpStatus.UNAUTHORIZED, "AUTH-013", "유저의 제공받은 리프레시토큰과 Redis의 리프레시토큰이 일치하지 않습니다."),

    FAILED_GENERATE_JWT_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-014", "JWT 토큰 생성에 실패했습니다."),
    FAILED_GENERATE_REGISTER_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-015", "레지스터 토큰 생성에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
