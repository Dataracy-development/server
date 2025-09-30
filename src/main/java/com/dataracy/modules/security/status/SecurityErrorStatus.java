package com.dataracy.modules.security.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum SecurityErrorStatus implements BaseErrorCode {
    UNEXPECTED_PRINCIPAL_TYPE_NOT_USER_DETAILS(HttpStatus.INTERNAL_SERVER_ERROR, "SECURITY-001", "인증 객체에 저장된 인증 객체의 principal가 CustomUserDetails가 아닙니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    SecurityErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
