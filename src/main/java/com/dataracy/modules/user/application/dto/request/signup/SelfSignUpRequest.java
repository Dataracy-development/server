/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.dto.request.signup;

import java.util.List;

import com.dataracy.modules.user.application.dto.request.PasswordConfirmable;

/**
 * 자체 회원가입을 위한 애플리케이션 요청 DTO
 *
 * @param email 이메일
 * @param password 비밀번호
 * @param passwordConfirm 비밀번호 확인
 * @param nickname 닉네임
 * @param authorLevelId 작성자 유형 아이디
 * @param occupationId 직업 아이디
 * @param topicIds 흥미있픽 토 아이디 리스트
 * @param visitSourceId 방문 경로 아이디
 * @param isAdTermsAgreed 광고 동의 여부
 */
public record SelfSignUpRequest(
    String email,
    String password,
    String passwordConfirm,
    String nickname,
    Long authorLevelId,
    Long occupationId,
    List<Long> topicIds,
    Long visitSourceId,
    Boolean isAdTermsAgreed)
    implements PasswordConfirmable {}
