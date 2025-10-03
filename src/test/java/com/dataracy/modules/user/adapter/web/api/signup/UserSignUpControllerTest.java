/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.adapter.web.api.signup;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
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

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = com.dataracy.modules.user.adapter.web.api.signup.UserSignUpController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class UserSignUpControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserSignUpWebMapper userSignUpWebMapper;

  @MockBean private SelfSignUpUseCase selfSignUpUseCase;

  @MockBean private OAuthSignUpUseCase oauthSignUpUseCase;

  @MockBean private com.dataracy.modules.common.util.CookieUtil cookieUtil;

  // 공통 모킹
  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
  @MockBean private JwtValidateUseCase jwtValidateUseCase;
  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

  @Test
  @DisplayName("signUpUserSelf API: 성공 - 201 Created와 JSON 응답 검증")
  void signUpUserSelfSuccess() throws Exception {
    // given
    SelfSignUpWebRequest webRequest =
        new SelfSignUpWebRequest(
            "test@example.com",
            "Password123!",
            "Password123!",
            "TestUser",
            1L,
            2L,
            List.of(1L, 2L),
            3L,
            true);

    SelfSignUpRequest requestDto =
        new SelfSignUpRequest(
            "test@example.com",
            "Password123!",
            "Password123!",
            "TestUser",
            1L,
            2L,
            List.of(1L, 2L),
            3L,
            true);

    RefreshTokenResponse responseDto = new RefreshTokenResponse("refresh-token", 3600L);

    given(userSignUpWebMapper.toApplicationDto(webRequest)).willReturn(requestDto);
    given(selfSignUpUseCase.signUpSelf(requestDto)).willReturn(responseDto);
    willDoNothing().given(cookieUtil).setCookie(any(), any(), any(), any(), anyInt());

    // when & then
    mockMvc
        .perform(
            post("/api/v1/signup/self")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(UserSuccessStatus.CREATED_USER.getCode()))
        .andExpect(jsonPath("$.message").value(UserSuccessStatus.CREATED_USER.getMessage()));

    then(userSignUpWebMapper).should().toApplicationDto(webRequest);
    then(selfSignUpUseCase).should().signUpSelf(requestDto);
    then(cookieUtil).should().setCookie(any(), any(), any(), any(), anyInt());
  }

  @Test
  @DisplayName("onboarding API: 성공 - 200 OK와 JSON 응답 검증")
  void onboardingSuccess() throws Exception {
    // given
    OnboardingWebRequest webRequest =
        new OnboardingWebRequest("TestUser", 1L, 2L, List.of(1L, 2L), 3L, true);

    OnboardingRequest requestDto =
        new OnboardingRequest("TestUser", 1L, 2L, List.of(1L, 2L), 3L, true);

    RefreshTokenResponse responseDto = new RefreshTokenResponse("refresh-token", 3600L);

    given(userSignUpWebMapper.toApplicationDto(webRequest)).willReturn(requestDto);
    given(oauthSignUpUseCase.signUpOAuth(anyString(), any(OnboardingRequest.class)))
        .willReturn(responseDto);
    willDoNothing().given(cookieUtil).setCookie(any(), any(), any(), any(), anyInt());

    // when & then
    mockMvc
        .perform(
            post("/api/v1/signup/oauth")
                .cookie(new Cookie("registerToken", "register-token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(UserSuccessStatus.CREATED_USER.getCode()))
        .andExpect(jsonPath("$.message").value(UserSuccessStatus.CREATED_USER.getMessage()));

    then(userSignUpWebMapper).should().toApplicationDto(webRequest);
    then(oauthSignUpUseCase).should().signUpOAuth(anyString(), any(OnboardingRequest.class));
    then(cookieUtil).should().setCookie(any(), any(), any(), any(), anyInt());
  }
}
