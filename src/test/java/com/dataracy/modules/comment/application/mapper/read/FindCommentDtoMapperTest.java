package com.dataracy.modules.comment.application.mapper.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.domain.model.Comment;

class FindCommentDtoMapperTest {

  private final FindCommentDtoMapper mapper = new FindCommentDtoMapper();

  @Nested
  @DisplayName("댓글 조회 응답 DTO")
  class FindComment {

    @Test
    @DisplayName("Comment → FindCommentResponse 변환 성공")
    void toCommentResponseDtoShouldMapCorrectly() {
      // given
      Comment comment = Comment.of(1L, 10L, 20L, "내용", null, 3L, LocalDateTime.now());

      // when
      FindCommentResponse dto =
          mapper.toResponseDto(comment, "닉네임", "profile.png", "실무자", 7L, true);

      // then
      assertAll(
          () -> assertThat(dto.id()).isEqualTo(comment.getId()),
          () -> assertThat(dto.creatorName()).isEqualTo("닉네임"),
          () -> assertThat(dto.userProfileImageUrl()).isEqualTo("profile.png"),
          () -> assertThat(dto.authorLevelLabel()).isEqualTo("실무자"),
          () -> assertThat(dto.childCommentCount()).isEqualTo(7L),
          () -> assertThat(dto.isLiked()).isTrue());
    }

    @Test
    @DisplayName("FindCommentResponse 변환 실패 → Comment가 null이면 NPE 발생")
    void toCommentResponseDtoShouldThrowWhenCommentIsNull() {
      // when
      NullPointerException ex =
          catchThrowableOfType(
              () -> mapper.toResponseDto(null, "nick", "url", "라벨", 0L, false),
              NullPointerException.class);

      // then
      assertThat(ex).isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("답글 조회 응답 DTO")
  class FindReplyComment {

    @Test
    @DisplayName("Comment → FindReplyCommentResponse 변환 성공")
    void toReplyCommentResponseDtoShouldMapCorrectly() {
      // given
      Comment reply = Comment.of(2L, 10L, 20L, "답글 내용", 1L, 1L, LocalDateTime.now());

      // when
      FindReplyCommentResponse dto =
          mapper.toResponseDto(reply, "작성자", "profile.png", "전문가", false);

      // then
      assertAll(
          () -> assertThat(dto.id()).isEqualTo(reply.getId()),
          () -> assertThat(dto.content()).isEqualTo("답글 내용"),
          () -> assertThat(dto.creatorName()).isEqualTo("작성자"),
          () -> assertThat(dto.authorLevelLabel()).isEqualTo("전문가"),
          () -> assertThat(dto.isLiked()).isFalse());
    }

    @Test
    @DisplayName("FindReplyCommentResponse 변환 실패 → Comment가 null이면 NPE 발생")
    void toReplyCommentResponseDtoShouldThrowWhenCommentIsNull() {
      // when
      NullPointerException ex =
          catchThrowableOfType(
              () -> mapper.toResponseDto(null, "nick", "url", "라벨", true),
              NullPointerException.class);

      // then
      assertThat(ex).isInstanceOf(NullPointerException.class);
    }
  }
}
