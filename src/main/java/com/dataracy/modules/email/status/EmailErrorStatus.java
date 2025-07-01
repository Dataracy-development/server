package com.dataracy.modules.email.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailErrorStatus implements BaseErrorCode {

    FAIL_VERIFY_EMAIL_CODE(HttpStatus.BAD_REQUEST, "EMAIL-001", "인증코드가 올바르지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
