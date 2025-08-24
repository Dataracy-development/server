package com.dataracy.modules.comment.application.mapper.read;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class FindCommentDtoMapperTest {

    private final FindCommentDtoMapper mapper = new FindCommentDtoMapper();

    @Test
    @DisplayName("Comment → FindCommentResponse 변환 성공")
    void toCommentResponseDtoSuccess() {
        // given
        Comment comment = Comment.of(
                1L,
                10L,
                20L,
                "내용",
                null,
                3L,
                LocalDateTime.now()
        );

        // when
        FindCommentResponse dto = mapper.toResponseDto(
                comment,
                "닉네임",
                "profile.png",
                "실무자",
                7L,
                true
        );

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.username()).isEqualTo("닉네임");
        assertThat(dto.userProfileUrl()).isEqualTo("profile.png");
        assertThat(dto.authorLevelLabel()).isEqualTo("실무자");
        assertThat(dto.childCommentCount()).isEqualTo(7L);
        assertThat(dto.isLiked()).isTrue();
    }

    @Test
    @DisplayName("Comment → FindReplyCommentResponse 변환 성공")
    void toReplyCommentResponseDtoSuccess() {
        // given
        Comment reply = Comment.of(
                2L,
                10L,
                20L,
                "답글 내용",
                1L,
                1L,
                LocalDateTime.now()
        );

        // when
        FindReplyCommentResponse dto = mapper.toResponseDto(
                reply,
                "작성자",
                "profile.png",
                "전문가",
                false
        );

        // then
        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.content()).isEqualTo("답글 내용");
        assertThat(dto.username()).isEqualTo("작성자");
        assertThat(dto.authorLevelLabel()).isEqualTo("전문가");
        assertThat(dto.isLiked()).isFalse();
    }

    @Test
    @DisplayName("FindCommentResponse 변환 실패 → Comment null 시 NPE 발생")
    void toCommentResponseDtoFailWhenNullComment() {
        // when
        NullPointerException ex = catchThrowableOfType(
                () -> mapper.toResponseDto(null, "nick", "url", "라벨", 0L, false),
                NullPointerException.class
        );

        // then
        assertThat(ex).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("FindReplyCommentResponse 변환 실패 → Comment null 시 NPE 발생")
    void toReplyCommentResponseDtoFailWhenNullComment() {
        // when
        NullPointerException ex = catchThrowableOfType(
                () -> mapper.toResponseDto(null, "nick", "url", "라벨", true),
                NullPointerException.class
        );

        // then
        assertThat(ex).isInstanceOf(NullPointerException.class);
    }
}
