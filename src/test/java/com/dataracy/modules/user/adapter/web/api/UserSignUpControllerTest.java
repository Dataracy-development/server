package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.user.adapter.web.api.signup.UserSignUpController;
import com.dataracy.modules.user.adapter.web.mapper.signup.UserSignUpWebMapper;
import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.command.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.command.signup.SelfSignUpUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UserSignUpController.class)
class UserSignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SelfSignUpUseCase selfSignUpUseCase;

    @MockBean
    private OAuthSignUpUseCase oauthSignUpUseCase;

    @MockBean
    private UserSignUpWebMapper userSignUpWebMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("signUpSelf API: 성공 → 201 Created + JSON 코드 검증 + refreshToken 쿠키 설정")
    void signUpSelfSuccess() throws Exception {
        // given
        SelfSignUpWebRequest webRequest = new SelfSignUpWebRequest(
                "user@test.com", "pw12345@", "pw12345@", "nick",
                1L, 2L, List.of(10L), 3L, true
        );
        SelfSignUpRequest reqDto = new SelfSignUpRequest(
                "user@test.com", "pw12345@", "pw12345@", "nick",
                1L, 2L, List.of(10L), 3L, true
        );

        given(userSignUpWebMapper.toApplicationDto(any(SelfSignUpWebRequest.class))).willReturn(reqDto);
        given(selfSignUpUseCase.signUpSelf(any(SelfSignUpRequest.class)))
                .willReturn(new RefreshTokenResponse("refresh-token", Instant.now().toEpochMilli() + 3600000));

        // when & then
        mockMvc.perform(post("/api/v1/signup/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.CREATED_USER.getCode()))
                .andExpect(cookie().value("refreshToken", "refresh-token"));

        then(userSignUpWebMapper).should().toApplicationDto(any(SelfSignUpWebRequest.class));
        then(selfSignUpUseCase).should().signUpSelf(any(SelfSignUpRequest.class));
    }

    @Test
    @DisplayName("signUpOAuth API: 성공 → 201 Created + JSON 코드 검증 + refreshToken 쿠키 설정")
    void signUpOAuthSuccess() throws Exception {
        String registerToken = "reg-token";
        OnboardingWebRequest webRequest = new OnboardingWebRequest(
                "nick", 1L, 2L, List.of(10L, 20L), 3L, true
        );
        OnboardingRequest reqDto = new OnboardingRequest(
                "nick", 1L, 2L, List.of(10L, 20L), 3L, true
        );

        given(userSignUpWebMapper.toApplicationDto(any(OnboardingWebRequest.class))).willReturn(reqDto);
        given(oauthSignUpUseCase.signUpOAuth(eq(registerToken), any(OnboardingRequest.class)))
                .willReturn(new RefreshTokenResponse("refresh-token", Instant.now().toEpochMilli() + 3600000));

        // when & then
        mockMvc.perform(post("/api/v1/signup/oauth")
                        .cookie(new Cookie("registerToken", registerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.CREATED_USER.getCode()))
                .andExpect(cookie().value("refreshToken", "refresh-token"));

        then(userSignUpWebMapper).should().toApplicationDto(any(OnboardingWebRequest.class));
        then(oauthSignUpUseCase).should().signUpOAuth(eq(registerToken), any(OnboardingRequest.class));
    }

    @Test
    @DisplayName("signUpSelf API: 실패 → topicIds = null → 400 Bad Request + 검증 메시지")
    void signUpSelfFailureValidation() throws Exception {
        // given: topicIds=null
        SelfSignUpWebRequest webRequest = new SelfSignUpWebRequest(
                "dup@test.com", "pw12345@", "pw12345@", "nick",
                1L, null, null, null, false
        );

        // when & then
        mockMvc.perform(post("/api/v1/signup/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webRequest)))
                .andExpect(status().isBadRequest());

        // 컨트롤러 진입 전이므로 mapper/useCase는 호출되지 않아야 함
        then(userSignUpWebMapper).shouldHaveNoInteractions();
        then(selfSignUpUseCase).shouldHaveNoInteractions();
    }

}
