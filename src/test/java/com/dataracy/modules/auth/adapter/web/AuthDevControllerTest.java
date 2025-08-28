package com.dataracy.modules.auth.adapter.web;

import com.dataracy.modules.auth.adapter.web.api.AuthDevController;
import com.dataracy.modules.auth.adapter.web.mapper.AuthDevWebMapper;
import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.auth.application.dto.request.RefreshTokenRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.domain.status.AuthSuccessStatus;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthDevController.class)
class AuthDevControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthDevWebMapper authDevWebMapper;

    @MockBean
    private SelfLoginUseCase selfLoginUseCase;

    @MockBean
    private ReIssueTokenUseCase reIssueTokenUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("개발용 로그인 성공 시 RefreshToken 반환")
    void loginDevSuccess() throws Exception {
        // given
        SelfLoginWebRequest webReq =
                new SelfLoginWebRequest("test@email.com", "password123@");
        SelfLoginRequest appReq =
                new SelfLoginRequest("test@email.com", "password123@");
        RefreshTokenResponse refreshResp =
                new RefreshTokenResponse("issued-refresh-token", 1209600000L);
        RefreshTokenWebResponse webResp =
                new RefreshTokenWebResponse("issued-refresh-token", 1209600000L);

        given(authDevWebMapper.toApplicationDto(any(SelfLoginWebRequest.class)))
                .willReturn(appReq);
        given(selfLoginUseCase.login(appReq))
                .willReturn(refreshResp);
        given(authDevWebMapper.toWebDto(refreshResp))
                .willReturn(webResp);

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(AuthSuccessStatus.OK_SELF_LOGIN.getCode()))
                .andExpect(jsonPath("$.message").value(AuthSuccessStatus.OK_SELF_LOGIN.getMessage()))
                .andExpect(jsonPath("$.data.refreshToken").value("issued-refresh-token"));
    }

    @Test
    @DisplayName("개발용 토큰 재발급 성공")
    void reIssueTokenDevSuccess() throws Exception {
        // given
        RefreshTokenWebRequest webReq =
                new RefreshTokenWebRequest("old-refresh");
        RefreshTokenRequest appReq =
                new RefreshTokenRequest("old-refresh");
        ReIssueTokenResponse reissueResp =
                new ReIssueTokenResponse("new-access", "new-refresh", 3600000L, 1209600000L);
        ReIssueTokenWebResponse webResp =
                new ReIssueTokenWebResponse("new-access", "new-refresh", 3600000L, 1209600000L);

        given(authDevWebMapper.toApplicationDto(any(RefreshTokenWebRequest.class)))
                .willReturn(appReq);
        given(reIssueTokenUseCase.reIssueToken("old-refresh"))
                .willReturn(reissueResp);
        given(authDevWebMapper.toWebDto(reissueResp))
                .willReturn(webResp);

        // when & then
        mockMvc.perform(post("/api/v1/auth/dev/token/re-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(AuthSuccessStatus.OK_RE_ISSUE_TOKEN.getCode()))
                .andExpect(jsonPath("$.message").value(AuthSuccessStatus.OK_RE_ISSUE_TOKEN.getMessage()))
                .andExpect(jsonPath("$.data.accessToken").value("new-access"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"))
                .andExpect(jsonPath("$.data.accessTokenExpiration").value(3600000L))
                .andExpect(jsonPath("$.data.refreshTokenExpiration").value(1209600000L));
    }
}
