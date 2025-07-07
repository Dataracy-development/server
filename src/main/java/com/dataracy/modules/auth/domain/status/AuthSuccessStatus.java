package com.dataracy.modules.auth.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessStatus implements BaseSuccessCode {
    OK_SELF_LOGIN(HttpStatus.OK, "200", "자체 로그인에 성공하였습니다."),
    OK_RE_ISSUE_TOKEN(HttpStatus.OK, "200", "토큰 재발급에 성공하였습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
