package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDateTime;

/**
 * 최근 최소 데이터 애플리케이션 응답 DTO
 *
 * @param id 데이터 ID
 * @param title 데이터 제목
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 데이터 업로더 유저 닉네임
 * @param dataThumbnailUrl 데이터 썸네일 URL
 * @param createdAt 생성 일시
 */
public record RecentMinimalDataResponse(
    Long id,
    String title,
    Long creatorId,
    String creatorName,
    String userProfileImageUrl,
    String dataThumbnailUrl,
    LocalDateTime createdAt) {}
