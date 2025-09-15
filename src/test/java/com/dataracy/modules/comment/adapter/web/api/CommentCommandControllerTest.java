package com.dataracy.modules.comment.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.comment.adapter.web.api.command.CommentCommandController;
import com.dataracy.modules.comment.adapter.web.mapper.command.CommandCommentWebMapper;
import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.response.command.UploadCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;
import com.dataracy.modules.comment.application.port.in.command.content.DeleteCommentUseCase;
import com.dataracy.modules.comment.application.port.in.command.content.ModifyCommentUseCase;
import com.dataracy.modules.comment.application.port.in.command.content.UploadCommentUseCase;
import com.dataracy.modules.comment.domain.status.CommentSuccessStatus;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CommentCommandController.class)
class CommentCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommandCommentWebMapper commandCommentWebMapper;

    @MockBean
    private UploadCommentUseCase uploadCommentUseCase;

    @MockBean
    private ModifyCommentUseCase modifyCommentUseCase;

    @MockBean
    private DeleteCommentUseCase deleteCommentUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    // ArgumentResolver Mock 처리
    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @BeforeEach
    void setupResolver() throws Exception {
        // 모든 @CurrentUserId Long 파라미터 → userId=1L 주입
        given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);

        // Jwt도 항상 통과
        given(jwtValidateUseCase.getUserIdFromToken(any()))
                .willReturn(1L);
    }

    @Test
    @DisplayName("댓글 작성 성공 → 201 Created + CREATED_COMMENT 반환")
    void uploadCommentSuccess() throws Exception {
        // given
        UploadCommentWebRequest webReq =
                new UploadCommentWebRequest("내용입니다", null);
        UploadCommentRequest appReq =
                new UploadCommentRequest("내용입니다", null);
        UploadCommentResponse appRes = new UploadCommentResponse(1L);
        UploadCommentWebResponse webRes = new UploadCommentWebResponse(1L);

        given(commandCommentWebMapper.toApplicationDto(any(UploadCommentWebRequest.class)))
                .willReturn(appReq);
        given(uploadCommentUseCase.uploadComment(any(), any(), any()))
                .willReturn(appRes);
        given(commandCommentWebMapper.toWebDto(appRes))
                .willReturn(webRes);

        // when & then
        mockMvc.perform(post("/api/v1/projects/{projectId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(CommentSuccessStatus.CREATED_COMMENT.getCode()))
                .andExpect(jsonPath("$.message").value(CommentSuccessStatus.CREATED_COMMENT.getMessage()))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("댓글 수정 성공 → 200 OK + MODIFY_COMMENT 반환")
    void modifyCommentSuccess() throws Exception {
        // given
        ModifyCommentWebRequest webReq =
                new ModifyCommentWebRequest("수정된 내용");
        ModifyCommentRequest appReq =
                new ModifyCommentRequest("수정된 내용");

        given(commandCommentWebMapper.toApplicationDto(any(ModifyCommentWebRequest.class)))
                .willReturn(appReq);
        willDoNothing().given(modifyCommentUseCase).modifyComment(any(), any(), any());

        // when & then
        mockMvc.perform(put("/api/v1/projects/{projectId}/comments/{commentId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CommentSuccessStatus.MODIFY_COMMENT.getCode()))
                .andExpect(jsonPath("$.message").value(CommentSuccessStatus.MODIFY_COMMENT.getMessage()));
    }

    @Test
    @DisplayName("댓글 삭제 성공 → 200 OK + DELETE_COMMENT 반환")
    void deleteCommentSuccess() throws Exception {
        // given
        willDoNothing().given(deleteCommentUseCase).deleteComment(1L, 2L);

        // when & then
        mockMvc.perform(delete("/api/v1/projects/{projectId}/comments/{commentId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CommentSuccessStatus.DELETE_COMMENT.getCode()))
                .andExpect(jsonPath("$.message").value(CommentSuccessStatus.DELETE_COMMENT.getMessage()));
    }
}
