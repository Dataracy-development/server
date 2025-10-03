/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.jpa.impl.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentCommandDbAdapterTest {

  @Mock private CommentJpaRepository repo;

  @InjectMocks private CommentCommandDbAdapter adapter;

  private CommentEntity dummyEntity() {
    return CommentEntity.of(1L, 1L, "내용", null);
  }

  @Nested
  @DisplayName("댓글 업로드")
  class UploadComment {

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
      assertAll(
          () -> assertThat(result).isNotNull(),
          () -> assertThat(result.getProjectId()).isEqualTo(1L),
          () -> assertThat(result.getContent()).isEqualTo("내용"));
      then(repo).should().save(any(CommentEntity.class));
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class ModifyComment {

    @Test
    @DisplayName("댓글 수정 성공")
    void modifyCommentSuccess() {
      // given
      CommentEntity entity = dummyEntity();
      ModifyCommentRequest req = new ModifyCommentRequest("수정된 댓글");
      given(repo.findById(1L)).willReturn(Optional.of(entity));
      given(repo.save(entity)).willReturn(entity);

      // when
      adapter.modifyComment(1L, 1L, req);

      // then
      assertThat(entity.getContent()).isEqualTo("수정된 댓글");
      then(repo).should().save(entity);
    }

    @Test
    @DisplayName("댓글 수정 실패 → 댓글 없음")
    void modifyCommentFailNotFound() {
      // given
      ModifyCommentRequest req = new ModifyCommentRequest("수정된 댓글");
      given(repo.findById(1L)).willReturn(Optional.empty());

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> adapter.modifyComment(1L, 1L, req), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
      then(repo).should(never()).save(any());
    }

    @Test
    @DisplayName("댓글 수정 실패 → 프로젝트 불일치")
    void modifyCommentFailMismatchProject() {
      // given
      CommentEntity entity = CommentEntity.of(99L, 1L, "내용", null);
      ModifyCommentRequest req = new ModifyCommentRequest("수정된 댓글");
      given(repo.findById(1L)).willReturn(Optional.of(entity));

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> adapter.modifyComment(1L, 1L, req), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
      then(repo).should(never()).save(any());
    }
  }

  @Nested
  @DisplayName("댓글 삭제")
  class DeleteComment {

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

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> adapter.deleteComment(1L, 1L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
      then(repo).should(never()).delete(any());
    }

    @Test
    @DisplayName("댓글 삭제 실패 → 프로젝트 불일치")
    void deleteCommentFailMismatchProject() {
      // given
      CommentEntity entity = CommentEntity.of(99L, 1L, "내용", null);
      given(repo.findById(1L)).willReturn(Optional.of(entity));

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> adapter.deleteComment(1L, 1L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
      then(repo).should(never()).delete(any());
    }
  }
}
