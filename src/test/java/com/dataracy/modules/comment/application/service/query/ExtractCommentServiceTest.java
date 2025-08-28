package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.port.out.query.extractor.ExtractCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ExtractCommentServiceTest {

    @Mock
    private ExtractCommentPort extractCommentPort;

    @InjectMocks
    private ExtractCommentService service;

    @Nested
    @DisplayName("작성자 아이디 반환")
    class ExtractUploaderId {

        @Test
        @DisplayName("댓글 존재 시 → userId 반환")
        void findUserIdShouldReturnUserIdWhenExists() {
            // given
            given(extractCommentPort.findUserIdByCommentId(1L))
                    .willReturn(Optional.of(100L));

            // when
            Long userId = service.findUserIdByCommentId(1L);

            // then
            assertThat(userId).isEqualTo(100L);
        }

        @Test
        @DisplayName("댓글이 존재하지 않으면 실패 → NOT_FOUND_COMMENT")
        void findUserIdShouldThrowWhenCommentNotFound() {
            // given
            Long commentId = 999L;
            given(extractCommentPort.findUserIdByCommentId(commentId))
                    .willReturn(Optional.empty());

            // when & then
            CommentException ex = catchThrowableOfType(
                    () -> service.findUserIdByCommentId(commentId),
                    CommentException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
        }
    }
}
