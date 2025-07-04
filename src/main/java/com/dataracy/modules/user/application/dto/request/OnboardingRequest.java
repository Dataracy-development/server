package com.dataracy.modules.user.application.dto.request;

import java.util.List;

/**
 * 소셜 로그인 후 추가정보 입력을 위한 도메인 요청 객체
 * @param nickname 닉네임
 * @param authorLevel 작성자 유형
 * @param occupation 직업
 * @param topics 흥미있는 도메인 리스트
 * @param visitSource 방문 경로
 * @param isAdTermsAgreed 광고 동의 여부
 */
public record OnboardingRequest(
        String nickname,
        String authorLevel,
        String occupation,
        List<String> topics,
        String visitSource,
        Boolean isAdTermsAgreed
) {}
