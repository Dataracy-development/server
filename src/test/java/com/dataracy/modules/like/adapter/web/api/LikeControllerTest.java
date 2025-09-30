package com.dataracy.modules.like.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.like.adapter.web.mapper.LikeWebMapper;
import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.command.LikeTargetUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.status.LikeSuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = LikeController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LikeWebMapper likeWebMapper;

    @MockBean
    private LikeTargetUseCase likeTargetUseCase;

    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @BeforeEach
    void setupResolver() {
        // 모든 @CurrentUserId Long 파라미터 → userId=1L 주입
        given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);

        // Jwt도 항상 통과
        given(jwtValidateUseCase.getUserIdFromToken(any()))
                .willReturn(1L);
    }

    @Test
    @DisplayName("프로젝트 좋아요 시 200 반환")
    void modifyTargetLikeNewProject() throws Exception {
        // given
        TargetLikeWebRequest web = new TargetLikeWebRequest(7L, "PROJECT", false);
        TargetLikeRequest dto = new TargetLikeRequest(7L, "PROJECT", false);

        given(likeWebMapper.toApplicationDto(any())).willReturn(dto);
        given(likeTargetUseCase.likeTarget(1L, dto)).willReturn(TargetType.PROJECT);

        // when & then
        mockMvc.perform(post("/api/v1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(web)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(LikeSuccessStatus.LIKE_PROJECT.getCode()))
                .andExpect(jsonPath("$.message").value(LikeSuccessStatus.LIKE_PROJECT.getMessage()));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 200 반환")
    void modifyTargetLikeCancelComment() throws Exception {
        // given
        TargetLikeWebRequest web = new TargetLikeWebRequest(77L, "COMMENT", true);
        TargetLikeRequest dto = new TargetLikeRequest(77L, "COMMENT", true);

        given(likeWebMapper.toApplicationDto(any())).willReturn(dto);
        given(likeTargetUseCase.likeTarget(1L, dto)).willReturn(TargetType.COMMENT);

        // when & then
        mockMvc.perform(post("/api/v1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(web)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(LikeSuccessStatus.UNLIKE_COMMENT.getCode()))
                .andExpect(jsonPath("$.message").value(LikeSuccessStatus.UNLIKE_COMMENT.getMessage()));
    }
}
