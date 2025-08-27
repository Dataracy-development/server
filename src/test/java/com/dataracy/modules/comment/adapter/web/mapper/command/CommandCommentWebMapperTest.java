package com.dataracy.modules.comment.adapter.web.mapper.command;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.response.command.UploadCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class CommandCommentWebMapperTest {

    private final CommandCommentWebMapper mapper = new CommandCommentWebMapper();

    @Nested
    @DisplayName("댓글 업로드")
    class UploadComment {

        @Test
        @DisplayName("UploadCommentWebRequest → UploadCommentRequest 변환 성공")
        void toApplicationDtoUploadComment() {
            // given
            UploadCommentWebRequest webReq = new UploadCommentWebRequest("내용", 1L);

            // when
            UploadCommentRequest appReq = mapper.toApplicationDto(webReq);

            // then
            assertThat(appReq.content()).isEqualTo("내용");
            assertThat(appReq.parentCommentId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("UploadCommentResponse → UploadCommentWebResponse 변환 성공")
        void toWebDtoUploadCommentResponse() {
            // given
            UploadCommentResponse responseDto = new UploadCommentResponse(10L);

            // when
            UploadCommentWebResponse webRes = mapper.toWebDto(responseDto);

            // then
            assertThat(webRes.id()).isEqualTo(10L);
        }

        @Test
        @DisplayName("toApplicationDto - UploadCommentWebRequest가 null이면 NPE 발생")
        void toApplicationDtoNullUploadRequest() {
            // given
            UploadCommentWebRequest req = null;

            // when
            NullPointerException ex = catchThrowableOfType(
                    () -> mapper.toApplicationDto(req),
                    NullPointerException.class
            );

            // then
            assertThat(ex).isNotNull();
        }

        @Test
        @DisplayName("toWebDto - id만 매핑 확인")
        void toWebDtoCheckOnlyIdMapping() {
            // given
            UploadCommentResponse response = new UploadCommentResponse(99L);

            // when
            UploadCommentWebResponse webRes = mapper.toWebDto(response);

            // then
            assertThat(webRes.id()).isEqualTo(99L);
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class ModifyComment {

        @Test
        @DisplayName("ModifyCommentWebRequest → ModifyCommentRequest 변환 성공")
        void toApplicationDtoModifyComment() {
            // given
            ModifyCommentWebRequest webReq = new ModifyCommentWebRequest("수정된 내용");

            // when
            ModifyCommentRequest appReq = mapper.toApplicationDto(webReq);

            // then
            assertThat(appReq.content()).isEqualTo("수정된 내용");
        }


        @Test
        @DisplayName("toApplicationDto - ModifyCommentWebRequest가 null이면 NPE 발생")
        void toApplicationDtoNullModifyRequest() {
            // given
            ModifyCommentWebRequest req = null;

            // when
            NullPointerException ex = catchThrowableOfType(
                    () -> mapper.toApplicationDto(req),
                    NullPointerException.class
            );

            // then
            assertThat(ex).isNotNull();
        }
    }
}
