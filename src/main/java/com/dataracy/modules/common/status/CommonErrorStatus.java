package com.dataracy.modules.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorStatus implements BaseErrorCode {

    // Global Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL-500", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL-400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLOBAL-401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "GLOBAL-403", "접근이 금지된 요청입니다."),
    NOT_FOUND_HANDLER(HttpStatus.NOT_FOUND, "GLOBAL-404", "요청 경로를 찾을 수 없습니다."),
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "GLOBAL-404", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL-405", "허용되지 않는 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "GLOBAL-415", "지원되지 않는 미디어 타입입니다."),

    // JSON Serialization Errors
    FAILED_SERIALIZING_JSON(HttpStatus.INTERNAL_SERVER_ERROR, "JSON-001", "JSON 직렬화에 실패했습니다."),
    FAILED_DESERIALIZING_JSON(HttpStatus.INTERNAL_SERVER_ERROR, "JSON-002", "JSON 역직렬화에 실패했습니다."),

    // Header Errors
    NOT_FOUND_REFRESH_TOKEN_IN_COOKIES(HttpStatus.NOT_FOUND, "COOKIE-001", "쿠키에 리프레시토큰이 존재하지 않습니다."),

    // File Errors
    OVER_MAXIMUM_IMAGE_FILE_SIZE(HttpStatus.BAD_REQUEST, "FILE-001", "이미지는 최대 10MB까지 업로드 가능합니다."),
    BAD_REQUEST_IMAGE_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE-002", "프로필 이미지는 jpg, jpeg, png 형식만 허용됩니다."),
    OVER_MAXIMUM_FILE_SIZE(HttpStatus.BAD_REQUEST, "FILE-003", "파일은 최대 50MB까지 업로드 가능합니다."),
    BAD_REQUEST_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE-004", "허용되지 않는 파일 형식입니다."),

    // Util Errors
    CAN_NOT_INSTANTIATE_COOKIE_UTILITY_CLASS(HttpStatus.INTERNAL_SERVER_ERROR, "UTIL-001", "CookieUtil은 인스턴스가 불가능한 클래스입니다."),
    CAN_NOT_INSTANTIATE_HEADER_UTILITY_CLASS(HttpStatus.INTERNAL_SERVER_ERROR, "UTIL-002", "HeaderUtil은 인스턴스가 불가능한 클래스입니다."),
    CAN_NOT_INSTANTIATE_FILE_UTILITY_CLASS(HttpStatus.INTERNAL_SERVER_ERROR, "UTIL-003", "FileUtil은 인스턴스가 불가능한 클래스입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
