package com.dataracy.modules.comment.domain.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CommentSuccessStatus 테스트")
class CommentSuccessStatusTest {

    @Test
    @DisplayName("모든 CommentSuccessStatus 값 확인")
    void allCommentSuccessStatuses_ShouldBeDefined() {
        // Then
        assertAll(
                () -> assertThat(CommentSuccessStatus.values()).hasSize(5),
                () -> assertThat(CommentSuccessStatus.CREATED_COMMENT).isNotNull(),
                () -> assertThat(CommentSuccessStatus.MODIFY_COMMENT).isNotNull(),
                () -> assertThat(CommentSuccessStatus.DELETE_COMMENT).isNotNull(),
                () -> assertThat(CommentSuccessStatus.GET_COMMENTS).isNotNull(),
                () -> assertThat(CommentSuccessStatus.GET_REPLY_COMMENTS).isNotNull()
        );
    }

    @Test
    @DisplayName("CommentSuccessStatus HTTP 상태 코드 확인")
    void commentSuccessStatuses_ShouldHaveCorrectHttpStatus() {
        // Then
        assertAll(
                () -> assertThat(CommentSuccessStatus.CREATED_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(CommentSuccessStatus.MODIFY_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(CommentSuccessStatus.DELETE_COMMENT.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(CommentSuccessStatus.GET_COMMENTS.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(CommentSuccessStatus.GET_REPLY_COMMENTS.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    @DisplayName("CommentSuccessStatus 코드 확인")
    void commentSuccessStatuses_ShouldHaveCorrectCode() {
        // Then
        assertAll(
                () -> assertThat(CommentSuccessStatus.CREATED_COMMENT.getCode()).isEqualTo("201"),
                () -> assertThat(CommentSuccessStatus.MODIFY_COMMENT.getCode()).isEqualTo("200"),
                () -> assertThat(CommentSuccessStatus.DELETE_COMMENT.getCode()).isEqualTo("200"),
                () -> assertThat(CommentSuccessStatus.GET_COMMENTS.getCode()).isEqualTo("200"),
                () -> assertThat(CommentSuccessStatus.GET_REPLY_COMMENTS.getCode()).isEqualTo("200")
        );
    }

    @Test
    @DisplayName("CommentSuccessStatus 메시지 확인")
    void commentSuccessStatuses_ShouldHaveCorrectMessage() {
        // Then
        assertAll(
                () -> assertThat(CommentSuccessStatus.CREATED_COMMENT.getMessage()).contains("댓글 작성이 완료되었습니다"),
                () -> assertThat(CommentSuccessStatus.MODIFY_COMMENT.getMessage()).contains("댓글 수정이 완료되었습니다"),
                () -> assertThat(CommentSuccessStatus.DELETE_COMMENT.getMessage()).contains("댓글 삭제가 완료되었습니다"),
                () -> assertThat(CommentSuccessStatus.GET_COMMENTS.getMessage()).contains("댓글 목록 조회가 완료되었습니다"),
                () -> assertThat(CommentSuccessStatus.GET_REPLY_COMMENTS.getMessage()).contains("답글 목록 조회가 완료되었습니다")
        );
    }
}
