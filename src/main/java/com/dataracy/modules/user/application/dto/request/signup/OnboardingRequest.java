package com.dataracy.modules.user.application.dto.request.signup;

import java.util.List;

/**
 * 소셜 로그인 후 추가정보 입력을 위한 애플리케이션 요청 객체
 *
 * @param nickname 닉네임
 * @param authorLevelId 작성자 유형 아이디
 * @param occupationId 직업 아이디
 * @param topicIds 흥미있는 토픽 아이디 리스트
 * @param visitSourceId 방문 경로 아이디
 * @param isAdTermsAgreed 광고 동의 여부
 */
public record OnboardingRequest(
    String nickname,
    Long authorLevelId,
    Long occupationId,
    List<Long> topicIds,
    Long visitSourceId,
    Boolean isAdTermsAgreed) {}
