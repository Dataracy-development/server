package com.dataracy.modules.comment.adapter.web.mapper.read;

import com.dataracy.modules.comment.adapter.web.response.read.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.read.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ReadCommentWebMapperTest {

    private final ReadCommentWebMapper mapper = new ReadCommentWebMapper();

    @Nested
    @DisplayName("댓글 조회")
    class FindComment {

        @Test
        @DisplayName("FindCommentResponse → FindCommentWebResponse 변환 성공")
        void toWebDtoComment() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FindCommentResponse responseDto = new FindCommentResponse(
                    1L,
                    "닉네임",
                    "실무자",
                    "profile.png",
                    "내용",
                    3L,
                    5L,
                    now,
                    true
            );

            // when
            FindCommentWebResponse webRes = mapper.toWebDto(responseDto);

            // then
            assertThat(webRes.id()).isEqualTo(1L);
            assertThat(webRes.username()).isEqualTo("닉네임");
            assertThat(webRes.authorLevelLabel()).isEqualTo("실무자");
            assertThat(webRes.userProfileUrl()).isEqualTo("profile.png");
            assertThat(webRes.content()).isEqualTo("내용");
            assertThat(webRes.likeCount()).isEqualTo(3L);
            assertThat(webRes.childCommentCount()).isEqualTo(5L);
            assertThat(webRes.createdAt()).isEqualTo(now);
            assertThat(webRes.isLiked()).isTrue();
        }

        @Test
        @DisplayName("FindCommentResponse → null 입력 시 NPE 발생")
        void toWebDtoNullCommentResponse() {
            // given
            FindCommentResponse dto = null;

            // when
            NullPointerException ex = catchThrowableOfType(
                    () -> mapper.toWebDto(dto),
                    NullPointerException.class
            );

            // then
            assertThat(ex).isNotNull();
        }
    }

    @Nested
    @DisplayName("답글 조회")
    class FindReplyComment {

        @Test
        @DisplayName("FindReplyCommentResponse → FindReplyCommentWebResponse 변환 성공")
        void toWebDtoReplyComment() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FindReplyCommentResponse responseDto = new FindReplyCommentResponse(
                    2L,
                    "작성자",
                    "전문가",
                    "profile2.png",
                    "답글 내용",
                    7L,
                    now,
                    false
            );

            // when
            FindReplyCommentWebResponse webRes = mapper.toWebDto(responseDto);

            // then
            assertThat(webRes.id()).isEqualTo(2L);
            assertThat(webRes.username()).isEqualTo("작성자");
            assertThat(webRes.authorLevelLabel()).isEqualTo("전문가");
            assertThat(webRes.userProfileUrl()).isEqualTo("profile2.png");
            assertThat(webRes.content()).isEqualTo("답글 내용");
            assertThat(webRes.likeCount()).isEqualTo(7L);
            assertThat(webRes.createdAt()).isEqualTo(now);
            assertThat(webRes.isLiked()).isFalse();
        }

        @Test
        @DisplayName("FindReplyCommentResponse → 정상 매핑 확인 (isLiked 포함)")
        void toWebDtoReplyResponseMapping() {
            // given
            FindReplyCommentResponse dto = new FindReplyCommentResponse(
                    5L,
                    "nick",
                    "라벨",
                    "url",
                    "내용",
                    2L,
                    LocalDateTime.now(),
                    true
            );

            // when
            FindReplyCommentWebResponse res = mapper.toWebDto(dto);

            // then
            assertThat(res.id()).isEqualTo(5L);
            assertThat(res.isLiked()).isTrue();
        }
    }
}
