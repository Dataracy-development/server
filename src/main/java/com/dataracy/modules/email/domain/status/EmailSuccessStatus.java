package com.dataracy.modules.email.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailSuccessStatus implements BaseSuccessCode {

    OK_SEND_EMAIL_CODE(HttpStatus.OK, "200", "이메일 인증 코드 전송에 성공했습니다."),
    OK_VERIFY_EMAIL_CODE(HttpStatus.OK, "200", "본인 인증이 완료되었습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
