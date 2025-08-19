package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

/**
 * 데이터셋과 연결된 프로젝트 애플리케이션 응답 DTO.
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param username 작성자 닉네임
 * @param topicLabel 주제 라벨
 * @param commentCount 댓글 수
 * @param likeCount 좋아요 수
 * @param viewCount 조회 수
 * @param createdAt 작성 시기
 */
public record ConnectedProjectResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
