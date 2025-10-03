/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.domain.status;

import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseSuccessCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserSuccessStatus implements BaseSuccessCode {
  CREATED_USER(HttpStatus.CREATED, "201", "회원가입에 성공했습니다"),
  OK_GET_USER_INFO(HttpStatus.OK, "200", "유저 정보 조회가 완료되었습니다."),
  OK_GET_OTHER_USER_INFO(HttpStatus.OK, "200", "타인유저 정보 조회가 완료되었습니다."),
  OK_NOT_DUPLICATED_NICKNAME(HttpStatus.OK, "200", "사용할 수 있는 닉네임입니다."),
  OK_CHANGE_PASSWORD(HttpStatus.OK, "200", "비밀번호를 변경했습니다."),
  OK_RESET_PASSWORD(HttpStatus.OK, "200", "비밀번호를 재설정했습니다."),
  OK_CONFIRM_PASSWORD(HttpStatus.OK, "200", "본인 인증되었습니다."),
  OK_GET_OTHER_EXTRA_PROJECTS(HttpStatus.OK, "200", "타인이 업로드한 프로젝트 목록 추가 조회가 완료되었습니다."),
  OK_GET_OTHER_EXTRA_DATASETS(HttpStatus.OK, "200", "타인이 업로드한 데이터셋 목록 추가 조회가 완료되었습니다."),
  OK_MODIFY_USER_INFO(HttpStatus.OK, "200", "회원 정보 수정이 완료되었습니다."),
  OK_WITHDRAW_USER(HttpStatus.OK, "200", "회원 탈퇴가 완료되었습니다."),
  OK_LOGOUT(HttpStatus.OK, "200", "회원 로그아웃이 완료되었습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
