package com.dataracy.modules.comment.adapter.jpa.impl.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExtractCommentDbAdapterTest {

  @Mock private CommentJpaRepository repo;

  @InjectMocks private ExtractCommentDbAdapter adapter;

  @Nested
  @DisplayName("작성자 아이디 추출")
  class ExtractUploaderId {

    @Test
    @DisplayName("댓글 ID로 UserId 찾기 성공")
    void findUserIdByCommentIdSuccess() {
      // given
      given(repo.findUserIdById(1L)).willReturn(Optional.of(100L));

      // when
      Optional<Long> result = adapter.findUserIdByCommentId(1L);

      // then
      assertThat(result).contains(100L);
    }

    @Test
    @DisplayName("findUserIdByCommentId → 존재하지 않는 댓글이면 Optional.empty 반환")
    void findUserIdByCommentIdEmpty() {
      // given
      given(repo.findUserIdById(1L)).willReturn(Optional.empty());

      // when
      Optional<Long> result = adapter.findUserIdByCommentId(1L);

      // then
      assertThat(result).isEmpty();
    }
  }
}
