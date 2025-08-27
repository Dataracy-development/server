package com.dataracy.modules.comment.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.comment.adapter.web.api.read.CommentReadController;
import com.dataracy.modules.comment.adapter.web.mapper.read.ReadCommentWebMapper;
import com.dataracy.modules.comment.adapter.web.response.read.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.read.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.port.in.query.read.FindCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.query.read.FindReplyCommentListUseCase;
import com.dataracy.modules.comment.domain.status.CommentSuccessStatus;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CommentReadController.class)
class CommentReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExtractHeaderUtil extractHeaderUtil;

    @MockBean
    private ReadCommentWebMapper readCommentWebMapper;

    @MockBean
    private FindCommentListUseCase findCommentListUseCase;

    @MockBean
    private FindReplyCommentListUseCase findReplyCommentListUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("댓글 목록 조회 성공 → 200 OK + GET_COMMENTS 반환")
    void findCommentsSuccess() throws Exception {
        // given
        FindCommentResponse appRes = new FindCommentResponse(
                1L,
                "닉네임",
                "등급",
                "url",
                "내용",
                0L,
                0L,
                LocalDateTime.now(),
                false);
        FindCommentWebResponse webRes = new FindCommentWebResponse(
                1L,
                "닉네임",
                "등급",
                "url",
                "내용",
                0L,
                0L,
                LocalDateTime.now(),
                false
        );

        given(extractHeaderUtil.extractAuthenticatedUserIdFromRequest(any()))
                .willReturn(1L);
        given(findCommentListUseCase.findComments(any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(appRes)));
        given(readCommentWebMapper.toWebDto(appRes))
                .willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CommentSuccessStatus.GET_COMMENTS.getCode()))
                .andExpect(jsonPath("$.message").value(CommentSuccessStatus.GET_COMMENTS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].id").value(1L));
    }

    @Test
    @DisplayName("답글 목록 조회 성공 → 200 OK + GET_REPLY_COMMENTS 반환")
    void findReplyCommentsSuccess() throws Exception {
        // given
        FindReplyCommentResponse appRes = new FindReplyCommentResponse(
                2L,
                "닉네임",
                "등급",
                "url",
                "내용",
                0L,
                LocalDateTime.now(),
                false
        );
        FindReplyCommentWebResponse webRes = new FindReplyCommentWebResponse(
                2L,
                "닉네임",
                "등급",
                "url",
                "내용",
                0L,
                LocalDateTime.now(),
                false
        );

        given(extractHeaderUtil.extractAuthenticatedUserIdFromRequest(any()))
                .willReturn(1L);
        given(findReplyCommentListUseCase.findReplyComments(any(), any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(appRes)));
        given(readCommentWebMapper.toWebDto(appRes))
                .willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}/comments/{commentId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CommentSuccessStatus.GET_REPLY_COMMENTS.getCode()))
                .andExpect(jsonPath("$.message").value(CommentSuccessStatus.GET_REPLY_COMMENTS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].id").value(2L));
    }
}
