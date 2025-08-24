package com.dataracy.modules.comment.adapter.jpa;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.adapter.jpa.impl.command.CommentCommandDbAdapter;
import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class CommentCommandDbAdapterTest {

    private CommentJpaRepository repo;
    private CommentCommandDbAdapter adapter;

    @BeforeEach
    void setup() {
        repo = mock(CommentJpaRepository.class);
        adapter = new CommentCommandDbAdapter(repo);
    }

    private CommentEntity dummyEntity() {
        return CommentEntity.of(1L, 1L, "내용", null);
    }

    @Test
    @DisplayName("댓글 업로드 성공")
    void uploadCommentSuccess() {
        // given
        Comment domain = Comment.of(null, 1L, 1L, "내용", null, 0L, null);
        CommentEntity saved = CommentEntity.of(1L, 1L, "내용", null);
        given(repo.save(any(CommentEntity.class))).willReturn(saved);

        // when
        Comment result = adapter.uploadComment(domain);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProjectId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void modifyCommentSuccess() {
        // given
        CommentEntity entity = dummyEntity();
        given(repo.findById(1L)).willReturn(Optional.of(entity));
        given(repo.save(entity)).willReturn(entity);

        // when
        adapter.modifyComment(1L, 1L, new ModifyCommentRequest("수정된"));

        // then
        assertThat(entity.getContent()).isEqualTo("수정된");
    }

    @Test
    @DisplayName("댓글 수정 실패 → 댓글 없음")
    void modifyCommentFailNotFound() {
        // given
        given(repo.findById(1L)).willReturn(Optional.empty());

        // when
        CommentException ex = catchThrowableOfType(
                () -> adapter.modifyComment(1L, 1L, new ModifyCommentRequest("수정된")),
                CommentException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("댓글 수정 실패 → 프로젝트 불일치")
    void modifyCommentFailMismatchProject() {
        // given
        CommentEntity entity = CommentEntity.of(99L, 1L, "내용", null);
        given(repo.findById(1L)).willReturn(Optional.of(entity));

        // when
        CommentException ex = catchThrowableOfType(
                () -> adapter.modifyComment(1L, 1L, new ModifyCommentRequest("수정된")),
                CommentException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentSuccess() {
        // given
        CommentEntity entity = dummyEntity();
        given(repo.findById(1L)).willReturn(Optional.of(entity));
        willDoNothing().given(repo).delete(entity);

        // when
        adapter.deleteComment(1L, 1L);

        // then
        then(repo).should().delete(entity);
    }

    @Test
    @DisplayName("댓글 삭제 실패 → 댓글 없음")
    void deleteCommentFailNotFound() {
        // given
        given(repo.findById(1L)).willReturn(Optional.empty());

        // when
        CommentException ex = catchThrowableOfType(
                () -> adapter.deleteComment(1L, 1L),
                CommentException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("댓글 삭제 실패 → 프로젝트 불일치")
    void deleteCommentFailMismatchProject() {
        // given
        CommentEntity entity = CommentEntity.of(99L, 1L, "내용", null);
        given(repo.findById(1L)).willReturn(Optional.of(entity));

        // when
        CommentException ex = catchThrowableOfType(
                () -> adapter.deleteComment(1L, 1L),
                CommentException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
}
