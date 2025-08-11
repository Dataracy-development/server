package com.dataracy.modules.project.application.dto.response.search;

/**
 * 실시간 프로젝트 검색 결과 응답 DTO
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param username 작성자 사용자명
 * @param projectThumbnailUrl 프로젝트 썸네일 URL
 */
public record RealTimeProjectResponse(
        Long id,
        String title,
        String username,
        String projectThumbnailUrl
) {}
