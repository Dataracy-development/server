package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.user.adapter.web.api.command.UseCommandController;
import com.dataracy.modules.user.adapter.web.mapper.command.UserCommandWebMapper;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.command.command.ModifyUserInfoUseCase;
import com.dataracy.modules.user.application.port.in.command.command.WithdrawUserUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UseCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCommandWebMapper userCommandWebMapper;

    @MockBean
    private ModifyUserInfoUseCase modifyUserInfoUseCase;

    @MockBean
    private WithdrawUserUseCase withdrawUserUseCase;

    // --- 공통 모킹 (항상 필요) ---
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @BeforeEach
    void setupResolver() throws Exception {
        // 모든 @CurrentUserId → userId=1L
        given(currentUserIdArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // Jwt도 항상 userId=1L 반환
        given(jwtValidateUseCase.getUserIdFromToken(any()))
                .willReturn(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 시 200 응답")
    void modifyUserInfoSuccess() throws Exception {
        // given
        Long userId = 1L;

        MockMultipartFile profileFile = new MockMultipartFile(
                "profileImageFile", "profile.png",
                MediaType.IMAGE_PNG_VALUE, "fake-content".getBytes()
        );

        MockMultipartFile webRequestPart = new MockMultipartFile(
                "webRequest", "webRequest", MediaType.APPLICATION_JSON_VALUE,
                """
                {
                  "nickname": "닉네임",
                  "authorLevelId": 2,
                  "occupationId": 3,
                  "topicIds": [10, 20],
                  "visitSourceId": 4,
                  "introductionText": "소개"
                }
                """.getBytes()
        );

        ModifyUserInfoRequest appReq = new ModifyUserInfoRequest(
                "닉네임", 2L, 3L, List.of(10L, 20L), 4L, "소개"
        );

        given(userCommandWebMapper.toApplicationDto(ArgumentMatchers.any())).willReturn(appReq);
        willDoNothing().given(modifyUserInfoUseCase).modifyUserInfo(eq(userId), any(), eq(appReq));

        // when & then
        mockMvc.perform(multipart("/api/v1/user")
                        .file(profileFile)
                        .file(webRequestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(req -> { req.setMethod("PUT"); return req; })) // PUT multipart 처리
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_MODIFY_USER_INFO.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_MODIFY_USER_INFO.getMessage()));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 시 200 응답")
    void withdrawUserSuccess() throws Exception {
        // given
        Long userId = 1L;
        willDoNothing().given(withdrawUserUseCase).withdrawUser(userId);

        // when & then
        mockMvc.perform(delete("/api/v1/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_WITHDRAW_USER.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_WITHDRAW_USER.getMessage()));
    }
}
