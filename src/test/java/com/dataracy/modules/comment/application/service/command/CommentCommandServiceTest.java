/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
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

import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;
import com.dataracy.modules.comment.application.port.out.command.create.UploadCommentPort;
import com.dataracy.modules.comment.application.port.out.command.delete.DeleteCommentPort;
import com.dataracy.modules.comment.application.port.out.command.event.SendCommentEventPort;
import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentPort;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentCommandServiceTest {

  @Mock private ReadCommentPort readCommentPort;

  @Mock private UploadCommentPort uploadCommentPort;

  @Mock private UpdateCommentPort updateCommentPort;

  @Mock private DeleteCommentPort deleteCommentPort;

  @Mock private SendCommentEventPort sendCommentEventPort;

  @InjectMocks private CommentCommandService service;

  @Nested
  @DisplayName("댓글 작성")
  class UploadComment {

    @Test
    @DisplayName("부모 없는 댓글 작성 성공 -> 댓글 작성 이벤트 발행 확인")
    void uploadRootCommentSuccess() {
      // given
      UploadCommentRequest req = new UploadCommentRequest("내용", null);
      Comment saved = Comment.of(1L, 1L, 1L, "내용", null, 0L, null);

      given(uploadCommentPort.uploadComment(any(Comment.class))).willReturn(saved);

      // when
      UploadCommentResponse res = service.uploadComment(1L, 1L, req);

      // then
      assertThat(res.id()).isEqualTo(1L);
      then(sendCommentEventPort).should().sendCommentUploadedEvent(1L);
    }

    @Test
    @DisplayName("부모 있는 댓글 작성 성공 -> 댓글 작성 이벤트 발행 확인")
    void uploadContinueCommentSuccess() {
      // given
      Comment parent = Comment.of(10L, 1L, 1L, "부모 댓글 내용", null, 0L, null);
      UploadCommentRequest req = new UploadCommentRequest("답글 내용", 10L);
      Comment saved = Comment.of(11L, 1L, 1L, "답글 내용", 10L, 0L, null);

      given(readCommentPort.findCommentById(10L)).willReturn(Optional.of(parent));
      given(uploadCommentPort.uploadComment(any(Comment.class))).willReturn(saved);

      // when
      UploadCommentResponse res = service.uploadComment(1L, 1L, req);

      // then
      assertThat(res.id()).isEqualTo(11L);
      then(sendCommentEventPort).should().sendCommentUploadedEvent(1L);
    }

    @Test
    @DisplayName("부모 댓글이 존재하지 않을 경우 업로드 실패 → NOT_FOUND_PARENT_COMMENT")
    void uploadCommentFailParentNotFound() {
      // given
      UploadCommentRequest request = new UploadCommentRequest("content", 999L);
      given(readCommentPort.findCommentById(999L)).willReturn(Optional.empty());

      // when & then
      CommentException ex =
          catchThrowableOfType(
              () -> service.uploadComment(1L, 1L, request), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT);
    }

    @Test
    @DisplayName("부모 댓글이 이미 대댓글일 경우 업로드 실패 → FORBIDDEN_REPLY_COMMENT")
    void uploadCommentFailForbiddenReply() {
      // given
      Comment parent = Comment.of(10L, 1L, 2L, "parent", 5L, 0L, null); // parentId != null → 이미 대댓글
      UploadCommentRequest request = new UploadCommentRequest("content", 10L);
      given(readCommentPort.findCommentById(10L)).willReturn(Optional.of(parent));

      // when & then
      CommentException ex =
          catchThrowableOfType(
              () -> service.uploadComment(1L, 1L, request), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT);
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class ModifyComment {

    @Test
    @DisplayName("댓글 수정 성공")
    void modifyCommentSuccess() {
      // given
      ModifyCommentRequest req = new ModifyCommentRequest("수정된 댓글");
      willDoNothing().given(updateCommentPort).modifyComment(1L, 1L, req);

      // when
      service.modifyComment(1L, 1L, req);

      // then
      then(updateCommentPort).should().modifyComment(1L, 1L, req);
    }

    @Test
    @DisplayName("댓글 수정 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void modifyCommentFailNotFound() {
      // given
      ModifyCommentRequest req = new ModifyCommentRequest("수정된 댓글");
      willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
          .given(updateCommentPort)
          .modifyComment(eq(1L), eq(99L), any(ModifyCommentRequest.class));

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.modifyComment(1L, 99L, req), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("댓글 수정 시 프로젝트와 댓글이 불일치 → MISMATCH_PROJECT_COMMENT")
    void modifyCommentFailMismatchProject() {
      // given
      ModifyCommentRequest req = new ModifyCommentRequest("수정된 댓글");
      willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
          .given(updateCommentPort)
          .modifyComment(eq(1L), eq(5L), any(ModifyCommentRequest.class));

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.modifyComment(1L, 5L, req), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
  }

  @Nested
  @DisplayName("댓글 삭제")
  class DeleteComment {

    @Test
    @DisplayName("댓글 삭제 성공 → 댓글 삭제 이벤트 발행 확인")
    void deleteCommentSuccess() {
      // given
      willDoNothing().given(deleteCommentPort).deleteComment(1L, 1L);

      // when
      service.deleteComment(1L, 1L);

      // then
      then(deleteCommentPort).should().deleteComment(1L, 1L);
      then(sendCommentEventPort).should().sendCommentDeletedEvent(1L);
    }

    @Test
    @DisplayName("댓글 삭제 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void deleteCommentFailNotFound() {
      // given
      willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
          .given(deleteCommentPort)
          .deleteComment(1L, 99L);

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.deleteComment(1L, 99L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("댓글 삭제 시 프로젝트와 댓글 불일치 → MISMATCH_PROJECT_COMMENT")
    void deleteCommentFailMismatchProject() {
      // given
      willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
          .given(deleteCommentPort)
          .deleteComment(1L, 77L);

      // when & then
      CommentException ex =
          catchThrowableOfType(() -> service.deleteComment(1L, 77L), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }

    @Test
    @DisplayName("댓글 삭제 시 commentId가 null → 정상 처리")
    void deleteCommentFailNullCommentId() {
      // given
      Long projectId = 1L;
      Long commentId = null;
      willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
          .given(deleteCommentPort)
          .deleteComment(projectId, commentId);

      // when & then
      CommentException ex =
          catchThrowableOfType(
              () -> service.deleteComment(projectId, commentId), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
      then(deleteCommentPort).should().deleteComment(projectId, commentId);
    }
  }
}
