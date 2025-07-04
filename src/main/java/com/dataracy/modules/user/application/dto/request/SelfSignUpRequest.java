package com.dataracy.modules.user.application.dto.request;

import java.util.List;

/**
 * 자체 회원가입을 위한 도메인 요청 객체
 * @param email 이메일
 * @param password 비밀번호
 * @param nickname 닉네임
 * @param authorLevel 작성자 유형
 * @param occupation 직업
 * @param topics 흥미있는 도메인 리스트
 * @param visitSource 방문경로
 * @param isAdTermsAgreed 광고 동의 여부
 */
public record SelfSignUpRequest(
        String email,
        String password,
        String nickname,
        String authorLevel,
        String occupation,
        List<String> topics,
        String visitSource,
        Boolean isAdTermsAgreed
) {}
