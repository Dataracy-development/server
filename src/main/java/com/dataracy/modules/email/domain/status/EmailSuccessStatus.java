package com.dataracy.modules.email.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailSuccessStatus implements BaseSuccessCode {
    OK_SEND_EMAIL_CODE_SIGN_UP(HttpStatus.OK, "200", "입력하신 이메일로 인증코드가 발송되었습니다. 인증 코드를 확인해주세요"),
    OK_SEND_EMAIL_CODE_PASSWORD_SEARCH(HttpStatus.OK, "200", "입력하신 이메일로 비밀번호 찾기 인증 코드가 발송되었습니다. 인증 코드를 확인해주세요"),
    OK_SEND_EMAIL_CODE_PASSWORD_RESET(HttpStatus.OK, "200", "입력하신 이메일로 비밀번호 재설정 인증 코드가 발송되었습니다. 인증 코드를 확인하여 비밀번호를 재설정해주세요"),

    OK_VERIFY_EMAIL_CODE(HttpStatus.OK, "200", "본인 인증이 완료되었습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
