/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.mapper.read.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.like.application.port.in.query.FindTargetIdsUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReadCommentServiceTest {

  @Mock private ReadCommentPort readCommentPort;

  @Spy private FindCommentDtoMapper mapper = new FindCommentDtoMapper();

  @Mock
  private com.dataracy.modules.comment.application.port.in.query.extractor
          .FindCommentUserInfoUseCase
      findCommentUserInfoUseCase;

  @Mock private FindTargetIdsUseCase findTargetIdsUseCase;

  @InjectMocks private ReadCommentService service;

  private Comment dummyComment(Long id, Long userId) {
    return Comment.of(id, 1L, userId, "content", null, 0L, LocalDateTime.now());
  }

  @Nested
  @DisplayName("댓글 목록 조회")
  class FindComments {

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void findCommentsShouldReturnPageOfComments() {
      // given
      Comment comment = dummyComment(1L, 1L);
      FindCommentWithReplyCountResponse response =
          new FindCommentWithReplyCountResponse(comment, 2L);
      Page<FindCommentWithReplyCountResponse> page = new PageImpl<>(List.of(response));

      given(readCommentPort.findComments(any(), any(Pageable.class))).willReturn(page);
      given(findCommentUserInfoUseCase.findCommentUserInfoBatch(any()))
          .willReturn(
              new com.dataracy.modules.comment.application.dto.response.support
                  .CommentLabelResponse(
                  Map.of(1L, "유저1"), Map.of(1L, "thumb.png"), Map.of(1L, "10"), Map.of(10L, "등급")));
      given(findTargetIdsUseCase.findLikedTargetIds(any(), any(), eq(TargetType.COMMENT)))
          .willReturn(List.of());

      // when
      Page<FindCommentResponse> result = service.findComments(1L, 1L, Pageable.ofSize(5));

      // then
      assertAll(
          () -> assertThat(result.getContent()).hasSize(1),
          () -> assertThat(result.getContent().get(0).creatorName()).isEqualTo("유저1"),
          () -> assertThat(result.getContent().get(0).content()).isEqualTo("content"));
    }

    @Test
    @DisplayName("댓글 목록 조회 실패 → User 정보 조회 중 예외 발생")
    void findCommentsShouldThrowWhenUserInfoFails() {
      // given
      Pageable pageable = PageRequest.of(0, 5);
      Comment comment = dummyComment(10L, 100L);
      Page<FindCommentWithReplyCountResponse> page =
          new PageImpl<>(List.of(new FindCommentWithReplyCountResponse(comment, 0L)));

      given(readCommentPort.findComments(1L, pageable)).willReturn(page);
      willThrow(new UserException(UserErrorStatus.NOT_FOUND_USER))
          .given(findCommentUserInfoUseCase)
          .findCommentUserInfoBatch(any());

      // when & then
      UserException ex =
          catchThrowableOfType(() -> service.findComments(1L, 1L, pageable), UserException.class);
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }
  }

  @Nested
  @DisplayName("답글 목록 조회")
  class FindReplyComments {

    @Test
    @DisplayName("답글 목록 조회 성공")
    void findReplyCommentsShouldReturnPageOfReplies() {
      // given
      Comment reply = Comment.of(2L, 1L, 1L, "답글", 1L, 0L, LocalDateTime.now());
      Page<Comment> page = new PageImpl<>(List.of(reply));

      given(readCommentPort.findReplyComments(any(), any(), any(Pageable.class))).willReturn(page);
      given(findCommentUserInfoUseCase.findCommentUserInfoBatch(any()))
          .willReturn(
              new com.dataracy.modules.comment.application.dto.response.support
                  .CommentLabelResponse(
                  Map.of(1L, "유저1"), Map.of(1L, "thumb.png"), Map.of(1L, "10"), Map.of(10L, "등급")));
      given(findTargetIdsUseCase.findLikedTargetIds(any(), any(), eq(TargetType.COMMENT)))
          .willReturn(List.of());

      // when
      Page<FindReplyCommentResponse> result =
          service.findReplyComments(1L, 1L, 1L, Pageable.ofSize(5));

      // then
      assertAll(
          () -> assertThat(result.getContent()).hasSize(1),
          () -> assertThat(result.getContent().get(0).content()).isEqualTo("답글"),
          () -> assertThat(result.getContent().get(0).creatorName()).isEqualTo("유저1"));
    }

    @Test
    @DisplayName("답글 목록 조회 실패 → ReadCommentPort 예외 발생 시 전파")
    void findReplyCommentsShouldThrowWhenPortThrows() {
      // given
      Pageable pageable = PageRequest.of(0, 5);
      willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
          .given(readCommentPort)
          .findReplyComments(1L, 10L, pageable);

      // when & then
      CommentException ex =
          catchThrowableOfType(
              () -> service.findReplyComments(1L, 1L, 10L, pageable), CommentException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("답글 목록 조회 실패 → 좋아요 정보 조회 중 예외 발생")
    void findReplyCommentsShouldThrowWhenLikeFails() {
      // given
      Pageable pageable = PageRequest.of(0, 5);
      Comment reply = dummyComment(20L, 200L);
      Page<Comment> page = new PageImpl<>(List.of(reply));

      given(readCommentPort.findReplyComments(1L, 10L, pageable)).willReturn(page);
      willThrow(new RuntimeException("Like Service Down"))
          .given(findTargetIdsUseCase)
          .findLikedTargetIds(1L, List.of(20L), TargetType.COMMENT);

      // when & then
      RuntimeException ex =
          catchThrowableOfType(
              () -> service.findReplyComments(1L, 1L, 10L, pageable), RuntimeException.class);
      assertThat(ex).hasMessage("Like Service Down");
    }
  }
}
