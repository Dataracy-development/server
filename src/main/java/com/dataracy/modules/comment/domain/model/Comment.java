package com.dataracy.modules.comment.domain.model;

import lombok.*;

import java.time.LocalDateTime;

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

    /**
     * 주어진 필드 값들로 새로운 Comment 인스턴스를 생성합니다.
     *
     * @param id 댓글의 고유 식별자
     * @param projectId 댓글이 속한 프로젝트의 식별자
     * @param userId 댓글 작성자의 식별자
     * @param content 댓글 내용
     * @param parentCommentId 부모 댓글의 식별자(답글이 아닌 경우 null)
     * @param likeCount 댓글의 좋아요 수
     * @param createdAt 댓글 생성 시각
     * @return 생성된 Comment 객체
     */
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
