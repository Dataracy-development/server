package com.dataracy.modules.comment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Comment 도메인 모델 테스트
 */
class CommentTest {

    @Test
    @DisplayName("기본 생성자로 Comment 인스턴스 생성")
    void createCommentWithDefaultConstructor() {
        // when
        Comment comment = new Comment();

        // then
        assertThat(comment).isNotNull();
    }

    @Test
    @DisplayName("Comment.of() 정적 팩토리 메서드로 인스턴스 생성")
    void createCommentWithOfMethod() {
        // given
        Long id = 1L;
        Long projectId = 10L;
        Long userId = 20L;
        String content = "테스트 댓글 내용";
        Long parentCommentId = null; // 루트 댓글
        Long likeCount = 5L;
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        Comment comment = Comment.of(id, projectId, userId, content, parentCommentId, likeCount, createdAt);

        // then
        assertAll(
                () -> assertThat(comment).isNotNull(),
                () -> assertThat(comment.getId()).isEqualTo(id),
                () -> assertThat(comment.getProjectId()).isEqualTo(projectId),
                () -> assertThat(comment.getUserId()).isEqualTo(userId),
                () -> assertThat(comment.getContent()).isEqualTo(content),
                () -> assertThat(comment.getParentCommentId()).isEqualTo(parentCommentId),
                () -> assertThat(comment.getLikeCount()).isEqualTo(likeCount),
                () -> assertThat(comment.getCreatedAt()).isEqualTo(createdAt)
        );
    }

    @Test
    @DisplayName("Comment.builder()로 인스턴스 생성")
    void createCommentWithBuilder() {
        // given
        Long id = 2L;
        Long projectId = 15L;
        Long userId = 25L;
        String content = "빌더로 생성한 댓글";
        Long parentCommentId = 1L; // 답글
        Long likeCount = 0L;
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        Comment comment = Comment.builder()
                .id(id)
                .projectId(projectId)
                .userId(userId)
                .content(content)
                .parentCommentId(parentCommentId)
                .likeCount(likeCount)
                .createdAt(createdAt)
                .build();

        // then
        assertAll(
                () -> assertThat(comment).isNotNull(),
                () -> assertThat(comment.getId()).isEqualTo(id),
                () -> assertThat(comment.getProjectId()).isEqualTo(projectId),
                () -> assertThat(comment.getUserId()).isEqualTo(userId),
                () -> assertThat(comment.getContent()).isEqualTo(content),
                () -> assertThat(comment.getParentCommentId()).isEqualTo(parentCommentId),
                () -> assertThat(comment.getLikeCount()).isEqualTo(likeCount),
                () -> assertThat(comment.getCreatedAt()).isEqualTo(createdAt)
        );
    }

    @Test
    @DisplayName("null 값들로 Comment 생성")
    void createCommentWithNullValues() {
        // given
        Long id = null;
        Long projectId = null;
        Long userId = null;
        String content = null;
        Long parentCommentId = null;
        Long likeCount = null;
        LocalDateTime createdAt = null;

        // when
        Comment comment = Comment.of(id, projectId, userId, content, parentCommentId, likeCount, createdAt);

        // then
        assertAll(
                () -> assertThat(comment).isNotNull(),
                () -> assertThat(comment.getId()).isNull(),
                () -> assertThat(comment.getProjectId()).isNull(),
                () -> assertThat(comment.getUserId()).isNull(),
                () -> assertThat(comment.getContent()).isNull(),
                () -> assertThat(comment.getParentCommentId()).isNull(),
                () -> assertThat(comment.getLikeCount()).isNull(),
                () -> assertThat(comment.getCreatedAt()).isNull()
        );
    }

    @Test
    @DisplayName("빈 문자열로 Comment 생성")
    void createCommentWithEmptyContent() {
        // given
        Long id = 3L;
        Long projectId = 20L;
        Long userId = 30L;
        String content = "";
        Long parentCommentId = null;
        Long likeCount = 0L;
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        Comment comment = Comment.of(id, projectId, userId, content, parentCommentId, likeCount, createdAt);

        // then
        assertAll(
                () -> assertThat(comment).isNotNull(),
                () -> assertThat(comment.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("긴 댓글 내용으로 Comment 생성")
    void createCommentWithLongContent() {
        // given
        Long id = 4L;
        Long projectId = 25L;
        Long userId = 35L;
        String content = "a".repeat(1000); // 1000자 댓글
        Long parentCommentId = null;
        Long likeCount = 10L;
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        Comment comment = Comment.of(id, projectId, userId, content, parentCommentId, likeCount, createdAt);

        // then
        assertAll(
                () -> assertThat(comment).isNotNull(),
                () -> assertThat(comment.getContent()).isEqualTo(content),
                () -> assertThat(comment.getContent()).hasSize(1000)
        );
    }
}