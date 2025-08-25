package com.dataracy.modules.like.application.service.command;

import com.dataracy.modules.comment.application.port.in.query.validate.ValidateCommentUseCase;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.out.command.LikeCommandPort;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.project.application.port.in.validate.ValidateProjectUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeCommandServiceTest {

    @Mock LikeCommandPort likeCommandPort;
    @Mock SendLikeEventPort sendLikeEventPort;
    @Mock ValidateProjectUseCase validateProjectUseCase;
    @Mock ValidateCommentUseCase validateCommentUseCase;

    @InjectMocks LikeCommandService service;

    @Captor ArgumentCaptor<com.dataracy.modules.like.domain.model.Like> likeCaptor;

    @Test
    @DisplayName("likeTarget_should_create_like_and_send_project_like_event_when_new_like_on_project")
    void likeTarget_should_create_like_and_send_project_like_event_when_new_like_on_project() {
        // given
        Long userId = 5L;
        TargetLikeRequest req = new TargetLikeRequest(100L, "PROJECT", false);
        // void 메서드 스텁은 willDoNothing().given(...)
        willDoNothing().given(validateProjectUseCase).validateProject(100L);

        // when
        TargetType result = service.likeTarget(userId, req);

        // then
        assertThat(result).isEqualTo(TargetType.PROJECT);
        then(likeCommandPort).should().save(likeCaptor.capture());
        com.dataracy.modules.like.domain.model.Like saved = likeCaptor.getValue();
        assertThat(saved.getTargetId()).isEqualTo(100L);
        assertThat(saved.getTargetType()).isEqualTo(TargetType.PROJECT);
        assertThat(saved.getUserId()).isEqualTo(5L);

        then(sendLikeEventPort).should().sendLikeEvent(TargetType.PROJECT, 100L, false);
    }

    @Test
    @DisplayName("likeTarget_should_cancel_like_and_send_comment_unlike_event_when_unlike_on_comment")
    void likeTarget_should_cancel_like_and_send_comment_unlike_event_when_unlike_on_comment() {
        // given
        Long userId = 9L;
        TargetLikeRequest req = new TargetLikeRequest(77L, "COMMENT", true);
        // void 메서드 스텁
        willDoNothing().given(validateCommentUseCase).validateComment(77L);

        // when
        TargetType result = service.likeTarget(userId, req);

        // then
        assertThat(result).isEqualTo(TargetType.COMMENT);
        then(likeCommandPort).should().cancelLike(9L, 77L, TargetType.COMMENT);
        then(sendLikeEventPort).should().sendLikeEvent(TargetType.COMMENT, 77L, true);
    }

    @Test
    @DisplayName("likeTarget_should_throw_when_invalid_target_type")
    void likeTarget_should_throw_when_invalid_target_type() {
        // given
        Long userId = 1L;
        TargetLikeRequest req = new TargetLikeRequest(7L, "INVALID", false);

        // when
        LikeException ex = catchThrowableOfType(() -> service.likeTarget(userId, req), LikeException.class);

        // then
        assertThat(ex).isNotNull();
        then(likeCommandPort).shouldHaveNoInteractions();
        then(sendLikeEventPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("likeTarget_should_propagate_exception_from_like_port")
    void likeTarget_should_propagate_exception_from_like_port() {
        // given
        Long userId = 2L;
        TargetLikeRequest req = new TargetLikeRequest(101L, "PROJECT", false);
        // 프로젝트 유효성은 통과(아무 일도 안 함)
        willDoNothing().given(validateProjectUseCase).validateProject(101L);
        // void 메서드에서 예외 전파
        willThrow(new LikeException(com.dataracy.modules.like.domain.status.LikeErrorStatus.FAIL_LIKE_PROJECT))
                .given(likeCommandPort).save(any());

        // when
        LikeException ex = catchThrowableOfType(() -> service.likeTarget(userId, req), LikeException.class);

        // then
        assertThat(ex).isNotNull();
    }
}
