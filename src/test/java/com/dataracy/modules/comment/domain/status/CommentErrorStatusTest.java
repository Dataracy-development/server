package com.dataracy.modules.comment.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CommentErrorStatus 테스트")
class CommentErrorStatusTest {

    @Test
    @DisplayName("모든 CommentErrorStatus 값 확인")
    void allCommentErrorStatuses_ShouldBeDefined() {
        // Then
        assertAll(
                () -> assertThat(CommentErrorStatus.values()).hasSize(6),
                () -> assertThat(CommentErrorStatus.FAIL_SAVE_COMMENT).isNotNull(),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_COMMENT).isNotNull(),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT).isNotNull(),
                () -> assertThat(CommentErrorStatus.NOT_MATCH_CREATOR).isNotNull(),
                () -> assertThat(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT).isNotNull(),
                () -> assertThat(CommentErrorStatus.MISMATCH_PROJECT_COMMENT).isNotNull()
        );
    }

    @Test
    @DisplayName("CommentErrorStatus HTTP 상태 코드 확인")
    void commentErrorStatuses_ShouldHaveCorrectHttpStatus() {
        // Then
        assertAll(
                () -> assertThat(CommentErrorStatus.FAIL_SAVE_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(CommentErrorStatus.NOT_MATCH_CREATOR.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(CommentErrorStatus.MISMATCH_PROJECT_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN)
        );
    }

    @Test
    @DisplayName("CommentErrorStatus 코드 확인")
    void commentErrorStatuses_ShouldHaveCorrectCode() {
        // Then
        assertAll(
                () -> assertThat(CommentErrorStatus.FAIL_SAVE_COMMENT.getCode()).isEqualTo("COMMENT-001"),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_COMMENT.getCode()).isEqualTo("COMMENT-002"),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT.getCode()).isEqualTo("COMMENT-003"),
                () -> assertThat(CommentErrorStatus.NOT_MATCH_CREATOR.getCode()).isEqualTo("COMMENT-004"),
                () -> assertThat(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT.getCode()).isEqualTo("COMMENT-005"),
                () -> assertThat(CommentErrorStatus.MISMATCH_PROJECT_COMMENT.getCode()).isEqualTo("COMMENT-006")
        );
    }

    @Test
    @DisplayName("CommentErrorStatus 메시지 확인")
    void commentErrorStatuses_ShouldHaveCorrectMessage() {
        // Then
        assertAll(
                () -> assertThat(CommentErrorStatus.FAIL_SAVE_COMMENT.getMessage()).contains("피드백 댓글 업로드에 실패했습니다"),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_COMMENT.getMessage()).contains("해당 피드백 댓글 리소스가 존재하지 않습니다"),
                () -> assertThat(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT.getMessage()).contains("해당 부모 댓글 리소스가 존재하지 않습니다"),
                () -> assertThat(CommentErrorStatus.NOT_MATCH_CREATOR.getMessage()).contains("작성자만 수정 및 삭제, 복원이 가능합니다"),
                () -> assertThat(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT.getMessage()).contains("답글에 다시 답글을 작성할 순 없습니다"),
                () -> assertThat(CommentErrorStatus.MISMATCH_PROJECT_COMMENT.getMessage()).contains("해당 프로젝트에 작성된 댓글이 아닙니다")
        );
    }
}
