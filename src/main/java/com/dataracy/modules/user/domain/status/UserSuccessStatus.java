package com.dataracy.modules.user.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessStatus implements BaseSuccessCode {

    CREATED_USER(HttpStatus.CREATED, "201", "회원가입에 성공했습니다"),
    OK_GET_USER_INFO(HttpStatus.OK, "200", "유저 정보 조회가 완료되었습니다."),
    OK_NOT_DUPLICATED_NICKNAME(HttpStatus.OK, "200", "사용할 수 있는 닉네임입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
