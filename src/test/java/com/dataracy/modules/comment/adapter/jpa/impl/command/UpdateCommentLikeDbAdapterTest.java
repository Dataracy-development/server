package com.dataracy.modules.comment.adapter.jpa.impl.command;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateCommentLikeDbAdapterTest {

  @Mock private CommentJpaRepository repo;

  @InjectMocks private UpdateCommentLikeDbAdapter adapter;

  @Nested
  @DisplayName("좋아요 업데이트")
  class UpdateLike {

    @Test
    @DisplayName("좋아요 증가 성공")
    void increaseLikeCountSuccess() {
      // given
      willDoNothing().given(repo).increaseLikeCount(1L);

      // when
      adapter.increaseLikeCount(1L);

      // then
      then(repo).should().increaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 감소 성공")
    void decreaseLikeCountSuccess() {
      // given
      willDoNothing().given(repo).decreaseLikeCount(1L);

      // when
      adapter.decreaseLikeCount(1L);

      // then
      then(repo).should().decreaseLikeCount(1L);
    }
  }
}
