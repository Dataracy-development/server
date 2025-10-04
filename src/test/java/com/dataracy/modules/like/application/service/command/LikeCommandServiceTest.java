package com.dataracy.modules.like.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.comment.application.port.in.query.validate.ValidateCommentUseCase;
import com.dataracy.modules.common.test.support.TestDataBuilder;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.out.command.LikeCommandPort;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.project.application.port.in.validate.ValidateProjectUseCase;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LikeCommandServiceTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  // Test constants
  private static final Long TEST_ID = 77L;

  @Mock private LikeCommandPort likeCommandPort;

  @Mock private SendLikeEventPort sendLikeEventPort;

  @Mock private ValidateProjectUseCase validateProjectUseCase;

  @Mock private ValidateCommentUseCase validateCommentUseCase;

  @InjectMocks private LikeCommandService service;

  @Captor private ArgumentCaptor<Like> likeCaptor;

  @Nested
  @DisplayName("타겟 좋아요")
  class LikeTarget {

    @Test
    @DisplayName("프로젝트에 새 좋아요 → 저장 및 이벤트 전송")
    void likeProjectSuccess() {
      // given
      Long userId = TestDataBuilder.RandomData.randomId();
      Long targetId = TestDataBuilder.RandomData.randomId();
      TargetLikeRequest req = new TargetLikeRequest(targetId, "PROJECT", false);
      willDoNothing().given(validateProjectUseCase).validateProject(targetId);

      // when
      TargetType result = service.likeTarget(userId, req);

      // then
      assertThat(result).isEqualTo(TargetType.PROJECT);
      then(likeCommandPort).should().save(likeCaptor.capture());
      Like saved = likeCaptor.getValue();
      assertAll(
          () -> assertThat(saved.getTargetId()).isEqualTo(targetId),
          () -> assertThat(saved.getTargetType()).isEqualTo(TargetType.PROJECT),
          () -> assertThat(saved.getUserId()).isEqualTo(userId));

      then(sendLikeEventPort).should().sendLikeEvent(TargetType.PROJECT, targetId, false);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 → cancelLike 및 이벤트 전송")
    void unlikeCommentSuccess() {
      // given
      Long userId = 9L;
      TargetLikeRequest req = new TargetLikeRequest(TEST_ID, "COMMENT", true);
      willDoNothing().given(validateCommentUseCase).validateComment(TEST_ID);

      // when
      TargetType result = service.likeTarget(userId, req);

      // then
      assertThat(result).isEqualTo(TargetType.COMMENT);
      then(likeCommandPort).should().cancelLike(9L, TEST_ID, TargetType.COMMENT);
      then(sendLikeEventPort).should().sendLikeEvent(TargetType.COMMENT, TEST_ID, true);
    }

    @Test
    @DisplayName("잘못된 타겟 타입 → LikeException 발생")
    void invalidTargetTypeThrows() {
      // given
      Long userId = 1L;
      TargetLikeRequest req = new TargetLikeRequest(7L, "INVALID", false);

      // when
      LikeException ex =
          catchThrowableOfType(() -> service.likeTarget(userId, req), LikeException.class);

      // then
      assertAll(
          () -> assertThat(ex).isNotNull(),
          () -> then(likeCommandPort).shouldHaveNoInteractions(),
          () -> then(sendLikeEventPort).shouldHaveNoInteractions());
    }

    @Test
    @DisplayName("LikeCommandPort.save에서 예외 발생 → 전파 확인")
    void likePortThrowsException() {
      // given
      Long userId = 2L;
      TargetLikeRequest req = new TargetLikeRequest(101L, "PROJECT", false);
      willDoNothing().given(validateProjectUseCase).validateProject(101L);
      willThrow(
              new LikeException(
                  com.dataracy.modules.like.domain.status.LikeErrorStatus.FAIL_LIKE_PROJECT))
          .given(likeCommandPort)
          .save(any());

      // when
      LikeException ex =
          catchThrowableOfType(() -> service.likeTarget(userId, req), LikeException.class);

      // then
      assertAll(() -> assertThat(ex).isNotNull());
    }
  }
}
