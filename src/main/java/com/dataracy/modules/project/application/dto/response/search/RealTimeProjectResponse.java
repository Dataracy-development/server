package com.dataracy.modules.project.application.dto.response.search;

/**
 * 실시간 프로젝트 검색 결과 애플리케이션 응답 DTO
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 작성자 프로필 이미지 URL
 * @param projectThumbnailUrl 프로젝트 썸네일 URL
 */
public record RealTimeProjectResponse(
        Long id,
        String title,
        Long creatorId,
        String creatorName,
        String userProfileImageUrl,
        String projectThumbnailUrl
) {}
