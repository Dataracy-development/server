package com.dataracy.modules.user.application.dto.response.read;

import com.dataracy.modules.user.domain.enums.RoleType;

import java.util.List;

/**
 * 사용자 개인정보 조회 애플리케이션 응답 DTO
 *
 * @param id 유저 아이디
 * @param role 유저 역할
 * @param email 이메일
 * @param nickname 닉네임
 * @param authorLevelLabel 작성자 유형 라벨
 * @param occupationLabel 직업 라벨
 * @param topicLabels 토픽 라벨 목록
 * @param visitSourceLabel 방문 경로 라벨
 * @param profileImageUrl 프로필 이미지 URL
 * @param introductionText 소개글
 */
public record GetUserInfoResponse(
        Long id,
        RoleType role,
        String email,
        String nickname,
        Long authorLevelId,
        String authorLevelLabel,
        Long occupationId,
        String occupationLabel,
        List<Long> topicIds,
        List<String> topicLabels,
        Long visitSourceId,
        String visitSourceLabel,
        String profileImageUrl,
        String introductionText
) {}
