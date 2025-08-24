package com.dataracy.modules.comment.application.service.command;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class CommentCommandServiceTest {

    private ReadCommentPort readCommentPort;
    private UploadCommentPort uploadCommentPort;
    private UpdateCommentPort updateCommentPort;
    private DeleteCommentPort deleteCommentPort;
    private SendCommentEventPort sendCommentEventPort;

    private CommentCommandService service;

    @BeforeEach
    void setup() {
        readCommentPort = mock(ReadCommentPort.class);
        uploadCommentPort = mock(UploadCommentPort.class);
        updateCommentPort = mock(UpdateCommentPort.class);
        deleteCommentPort = mock(DeleteCommentPort.class);
        sendCommentEventPort = mock(SendCommentEventPort.class);

        service = new CommentCommandService(
                readCommentPort,
                uploadCommentPort,
                updateCommentPort,
                deleteCommentPort,
                sendCommentEventPort
        );
    }

    @Test
    @DisplayName("부모 없는 댓글 작성 성공 -> 댓글 작성 이벤트 발행 확인")
    void uploadRootCommentSuccess() {
        UploadCommentRequest req = new UploadCommentRequest(
                "내용",
                null
        );
        Comment saved = Comment.of(
                1L,
                1L,
                1L,
                "내용",
                null,
                0L,
                null
        );

        given(uploadCommentPort.uploadComment(any(Comment.class))).willReturn(saved);

        UploadCommentResponse res = service.uploadComment(1L, 1L, req);

        assertThat(res.id()).isEqualTo(1L);
        then(sendCommentEventPort).should().sendCommentUploadedEvent(1L);
    }

    @Test
    @DisplayName("부모 있는 댓글 작성 성공 -> 댓글 작성 이벤트 발행 확인")
    void uploadContinueCommentSuccess() {
        // given
        Comment parent = Comment.of(10L, 1L, 1L, "부모 댓글 내용", null, 0L, null);
        UploadCommentRequest req = new UploadCommentRequest("답글 내용", 10L);
        Comment saved = Comment.of(
                11L,
                1L,
                1L,
                "답글 내용",
                10L,
                0L,
                null
        );

        given(uploadCommentPort.uploadComment(any(Comment.class))).willReturn(saved);
        given(readCommentPort.findCommentById(10L)).willReturn(Optional.of(parent));

        UploadCommentResponse res = service.uploadComment(1L, 1L, req);

        assertThat(res.id()).isEqualTo(11L);
        then(sendCommentEventPort).should().sendCommentUploadedEvent(1L);
    }

    @Test
    @DisplayName("부모 댓글이 존재하지 않을 경우 업로드 실패 → NOT_FOUND_PARENT_COMMENT")
    void uploadCommentFailParentNotFound() {
        // given
        UploadCommentRequest request = new UploadCommentRequest("content", 999L);
        given(readCommentPort.findCommentById(999L)).willReturn(Optional.empty());

        CommentException ex = catchThrowableOfType(
                () -> service.uploadComment(1L, 1L, request),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT);
    }

    @Test
    @DisplayName("부모 댓글이 이미 대댓글일 경우 업로드 실패 → FORBIDDEN_REPLY_COMMENT")
    void uploadCommentFailForbiddenReply() {
        // given
        Comment parent = Comment.of(10L, 1L, 2L, "parent", 5L, 0L, null);
        UploadCommentRequest request = new UploadCommentRequest("content", 10L);

        given(readCommentPort.findCommentById(10L)).willReturn(Optional.of(parent));

        CommentException ex = catchThrowableOfType(
                () -> service.uploadComment(1L, 1L, request),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void modifyCommentSuccess() {
        ModifyCommentRequest req = new ModifyCommentRequest("수정된");
        willDoNothing().given(updateCommentPort).modifyComment(1L, 1L, req);

        service.modifyComment(1L, 1L, req);

        then(updateCommentPort).should().modifyComment(1L, 1L, req);
    }

    @Test
    @DisplayName("댓글 수정 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void modifyCommentFailNotFound() {
        // given
        willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
                .given(updateCommentPort).modifyComment(eq(1L), eq(99L), any(ModifyCommentRequest.class));

        // when & then
        CommentException ex = catchThrowableOfType(
                () -> service.modifyComment(1L, 99L, new ModifyCommentRequest("new content")),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("댓글 수정 시 프로젝트와 댓글이 불일치 → MISMATCH_PROJECT_COMMENT")
    void modifyCommentFailMismatchProject() {
        // given
        willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
                .given(updateCommentPort).modifyComment(eq(1L), eq(5L), any(ModifyCommentRequest.class));

        // when & then
        CommentException ex = catchThrowableOfType(
                () -> service.modifyComment(1L, 5L, new ModifyCommentRequest("new content")),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }

    @Test
    @DisplayName("댓글 삭제 성공 → 댓글 삭제 이벤트 발행 확인")
    void deleteCommentSuccess() {
        willDoNothing().given(deleteCommentPort).deleteComment(1L, 1L);

        service.deleteComment(1L, 1L);

        then(deleteCommentPort).should().deleteComment(1L, 1L);
        then(sendCommentEventPort).should().sendCommentDeletedEvent(1L);
    }

    @Test
    @DisplayName("댓글 삭제 시 대상 댓글이 없으면 실패 → NOT_FOUND_COMMENT")
    void deleteCommentFailNotFound() {
        // given
        willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
                .given(deleteCommentPort).deleteComment(1L, 99L);

        // when & then
        CommentException ex = catchThrowableOfType(
                () -> service.deleteComment(1L, 99L),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("댓글 삭제 시 프로젝트와 댓글 불일치 → MISMATCH_PROJECT_COMMENT")
    void deleteCommentFailMismatchProject() {
        // given
        willThrow(new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT))
                .given(deleteCommentPort).deleteComment(1L, 77L);

        // when & then
        CommentException ex = catchThrowableOfType(
                () -> service.deleteComment(1L, 77L),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
}
