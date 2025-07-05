package com.dataracy.modules.email.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailErrorStatus implements BaseErrorCode {
    FAIL_SEND_EMAIL_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-001", "인증번호 발송에 실패했습니다. 다시 시도해주세요"),
    FAIL_VERIFY_EMAIL_CODE(HttpStatus.BAD_REQUEST, "EMAIL-002", "인증번호를 확인해주세요"),
    EXPIRED_EMAIL_CODE(HttpStatus.BAD_REQUEST, "EMAIL-003", "인증 시간이 초과되었습니다. 인증번호를 재요청 해주세요"),
    INVALID_EMAIL_SEND_TYPE(HttpStatus.BAD_REQUEST, "EMAIL-004", "이메일 전송 목적이 올바르지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
