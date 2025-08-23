package com.dataracy.modules.auth.adapter.web;

import com.dataracy.modules.auth.adapter.web.api.AuthController;
import com.dataracy.modules.auth.adapter.web.mapper.AuthWebMapper;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.domain.status.AuthSuccessStatus;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthWebMapper authWebMapper;

    @MockBean
    private SelfLoginUseCase selfLoginUseCase;

    @MockBean
    private ReIssueTokenUseCase reIssueTokenUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("자체 로그인 성공 시 RefreshToken 쿠키 발급")
    void loginSuccess() throws Exception {
        // given
        SelfLoginWebRequest webReq =
                new SelfLoginWebRequest("test@email.com", "password123@");
        SelfLoginRequest appReq =
                new SelfLoginRequest("test@email.com", "password123@");
        RefreshTokenResponse refreshResponse =
                new RefreshTokenResponse("refresh-token-abc", 1209600000L);

        given(authWebMapper.toApplicationDto(any(SelfLoginWebRequest.class))).willReturn(appReq);
        given(selfLoginUseCase.login(appReq)).willReturn(refreshResponse);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(AuthSuccessStatus.OK_SELF_LOGIN.getCode()))
                .andExpect(jsonPath("$.message").value(AuthSuccessStatus.OK_SELF_LOGIN.getMessage()))
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    @DisplayName("RefreshToken 쿠키로 재발급 성공")
    void reIssueTokenSuccess() throws Exception {
        // given
        ReIssueTokenResponse reissueResponse =
                new ReIssueTokenResponse("new-access", "new-refresh", 60000L, 120000L);

        given(reIssueTokenUseCase.reIssueToken("refresh-token-abc"))
                .willReturn(reissueResponse);

        // when & then
        mockMvc.perform(post("/api/v1/auth/token/re-issue")
                        .cookie(new Cookie("refreshToken", "refresh-token-abc")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(AuthSuccessStatus.OK_RE_ISSUE_TOKEN.getCode()))
                .andExpect(jsonPath("$.message").value(AuthSuccessStatus.OK_RE_ISSUE_TOKEN.getMessage()))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));
    }
}
