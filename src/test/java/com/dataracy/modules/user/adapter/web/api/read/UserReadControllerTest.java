package com.dataracy.modules.user.adapter.web.api.read;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.user.adapter.web.mapper.read.UserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = com.dataracy.modules.user.adapter.web.api.read.UserReadController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class UserReadControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserReadWebMapper userReadWebMapper;

  @MockBean private GetUserInfoUseCase getUserInfoUseCase;

  // 공통 모킹
  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
  @MockBean private JwtValidateUseCase jwtValidateUseCase;
  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;
  @MockBean private com.dataracy.modules.common.logging.ApiLogger apiLogger;
  @MockBean private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

  @BeforeEach
  void setupResolver() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new com.dataracy.modules.user.adapter.web.api.read.UserReadController(
                    userReadWebMapper, getUserInfoUseCase))
            .setCustomArgumentResolvers(currentUserIdArgumentResolver)
            .build();

    // 모든 @CurrentUserId → userId=1L
    given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
    given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
  }

  @Test
  @DisplayName("getUserInfo API: 성공 - 200 OK와 JSON 응답 검증")
  void getUserInfoSuccess() throws Exception {
    // given
    Long userId = 1L;

    GetUserInfoResponse responseDto =
        new GetUserInfoResponse(
            1L,
            RoleType.ROLE_USER,
            "test@example.com",
            "Test User",
            1L,
            "Expert",
            2L,
            "Developer",
            List.of(1L, 2L),
            List.of("AI", "Data"),
            3L,
            "Google",
            "profile.jpg",
            "Introduction");

    GetUserInfoWebResponse webResponse =
        new GetUserInfoWebResponse(
            1L,
            RoleType.ROLE_USER,
            "test@example.com",
            "Test User",
            1L,
            "Expert",
            2L,
            "Developer",
            List.of(1L, 2L),
            List.of("AI", "Data"),
            3L,
            "Google",
            "profile.jpg",
            "Introduction");

    given(getUserInfoUseCase.getUserInfo(userId)).willReturn(responseDto);
    given(userReadWebMapper.toWebDto(responseDto)).willReturn(webResponse);

    // when & then
    mockMvc
        .perform(get("/api/v1/user").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(
            result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
        .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_GET_USER_INFO.getCode()))
        .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_GET_USER_INFO.getMessage()))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.nickname").value("Test User"));

    then(getUserInfoUseCase).should().getUserInfo(userId);
    then(userReadWebMapper).should().toWebDto(responseDto);
  }
}
