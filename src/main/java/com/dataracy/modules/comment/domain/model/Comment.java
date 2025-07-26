package com.dataracy.modules.comment.domain.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * comment 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment {
    private Long id;
    private Long projectId;
    private Long userId;
    private String content;
    private Long parentCommentId;
    private Long likeCount;
    private LocalDateTime createdAt;

    public static Comment of(
            Long id,
            Long projectId,
            Long userId,
            String content,
            Long parentCommentId,
            Long likeCount,
            LocalDateTime createdAt
    ) {
        return Comment.builder()
                .id(id)
                .projectId(projectId)
                .userId(userId)
                .content(content)
                .parentCommentId(parentCommentId)
                .likeCount(likeCount)
                .createdAt(createdAt)
                .build();
    }
}
