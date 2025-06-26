package com.dataracy.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorStatus implements BaseErrorCode {

    // Global Errors
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL-500", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL-400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLOBAL-401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "GLOBAL-403", "접근이 금지된 요청입니다."),
    _NOT_FOUND_HANDLER(HttpStatus.NOT_FOUND, "GLOBAL-404", "요청 경로를 찾을 수 없습니다."),
    _NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "GLOBAL-404", "요청한 리소스를 찾을 수 없습니다."),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL-405", "허용되지 않는 HTTP 메서드입니다."),
    _UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "GLOBAL-415", "지원되지 않는 미디어 타입입니다."),

    // Redis Errors
    _FAILED_SAVE_REFRESH_TOKEN_IN_REDIS(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS-001", "리프레시 토큰의 Redis 저장에 실패했습니다."),
    _REFRESH_TOKEN_NOT_FOUND_IN_REDIS(HttpStatus.NOT_FOUND, "REDIS-002", "Redis에서 해당 유저의 리프레시 토큰을 찾을 수 없습니다."),
    _REFRESH_TOKEN_USER_MISMATCH_IN_REDIS(HttpStatus.UNAUTHORIZED, "REDIS-003", "제공받은 리프레시토큰과 Redis의 리프레시토큰이 일치하지 않습니다."),

    // JSON Serialization Errors
    _FAILED_SERIALIZING_JSON(HttpStatus.INTERNAL_SERVER_ERROR, "JSON-001", "JSON 직렬화에 실패했습니다."),
    _FAILED_DESERIALIZING_JSON(HttpStatus.INTERNAL_SERVER_ERROR, "JSON-002", "JSON 역직렬화에 실패했습니다."),

    // Header Errors
    _NOT_FOUND_REFRESH_TOKEN_IN_COOKIES(HttpStatus.NOT_FOUND, "COOKIE-001", "쿠키에 리프레시토큰이 존재하지 않습니다."),

    // File Errors
    _OVER_MAXIMUM_IMAGE_FILE_SIZE(HttpStatus.BAD_REQUEST, "FILE-001", "이미지는 최대 10MB까지 업로드 가능합니다."),
    _BAD_REQUEST_IMAGE_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE-002", "프로필 이미지는 jpg, jpeg, png 형식만 허용됩니다."),
    _OVER_MAXIMUM_FILE_SIZE(HttpStatus.BAD_REQUEST, "FILE-003", "파일은 최대 50MB까지 업로드 가능합니다."),
    _BAD_REQUEST_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE-004", "허용되지 않는 파일 형식입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
