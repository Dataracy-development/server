package com.dataracy.modules.user.application.dto.response.read;

/**
 * 타인 사용자 개인정보 조회 애플리케이션 응답 DTO
 *
 * @param id 유저 아이디
 * @param nickname 닉네임
 * @param authorLevelLabel 작성자 유형 라벨
 * @param occupationLabel 직업 라벨
 * @param profileImageUrl 프로필 이미지 URL
 * @param introductionText 소개글
 */
public record GetOtherUserInfoResponse(
        Long id,
        String nickname,
        String authorLevelLabel,
        String occupationLabel,
        String profileImageUrl,
        String introductionText
) {}
