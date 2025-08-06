package com.dataracy.modules.auth.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorStatus implements BaseErrorCode {
    // 인증 기본
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "AUTH-001", "로그인이 필요한 요청입니다."),
    NOT_FOUND_ACCESS_TOKEN_IN_HEADER(HttpStatus.UNAUTHORIZED, "AUTH-002", "요청 헤더에 액세스 토큰이 없습니다."),
    BAD_REQUEST_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "AUTH-003", "잘못된 토큰 유형입니다. REGISTER, ACCESS, REFRESH 중 하나여야 합니다."),
    BAD_REQUEST_LOGIN(HttpStatus.BAD_REQUEST, "AUTH-004", "이메일 또는 비밀번호를 확인해주세요"),

    // 토큰 만료
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-005", "토큰이 만료되었습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-006", "액세스 토큰이 만료되었습니다. 토큰을 재발급해주세요."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-007", "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요."),
    EXPIRED_REGISTER_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-008", "회원가입을 위한 유효시간이 만료되었습니다. 다시 소셜 로그인을 시도해주세요."),
    EXPIRED_RESET_PASSWORD_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-009", "비밀번호 재설정을 위한 유효시간이 만료되었습니다."),

    // 토큰 검증 실패
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-010", "토큰이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-011", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-012", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_REGISTER_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-013", "유효하지 않은 레지스터 토큰입니다."),
    INVALID_RESET_PASSWORD_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-014", "유효하지 않은 비밀번호 재설정 토큰입니다."),

    // Redis 및 일치 오류
    REFRESH_TOKEN_USER_MISMATCH_IN_REDIS(HttpStatus.UNAUTHORIZED, "AUTH-015", "Redis의 리프레시 토큰과 일치하지 않습니다."),

    // 토큰 생성 실패
    FAILED_GENERATE_ACCESS_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-016", "액세스 토큰 생성에 실패했습니다."),
    FAILED_GENERATE_REFRESH_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-017", "리프레시 토큰 생성에 실패했습니다."),
    FAILED_GENERATE_REGISTER_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-018", "레지스터 토큰 생성에 실패했습니다."),
    FAILED_GENERATE_RESET_PASSWORD_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-019", "비밀번호 재설정 토큰 생성에 실패했습니다."),
    SHORT_JWT_SECRET(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-020", "JWT Secret 키는 최소 32자 이상이어야 합니다."),

    // 소셜 타입 오류
    NOT_SUPPORTED_SOCIAL_PROVIDER_TYPE(HttpStatus.BAD_REQUEST, "AUTH-021", "지원되지 않는 소셜 로그인 유형입니다. (구글, 카카오만 가능)")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
