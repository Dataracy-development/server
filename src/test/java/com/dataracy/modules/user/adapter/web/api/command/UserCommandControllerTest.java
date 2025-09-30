package com.dataracy.modules.user.adapter.web.api.command;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.adapter.web.mapper.command.UserCommandWebMapper;
import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.command.command.LogoutUserUseCase;
import com.dataracy.modules.user.application.port.in.command.command.ModifyUserInfoUseCase;
import com.dataracy.modules.user.application.port.in.command.command.WithdrawUserUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import jakarta.servlet.http.Cookie;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = com.dataracy.modules.user.adapter.web.api.command.UseCommandController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class, com.dataracy.modules.common.config.web.MultipartJackson2HttpMessageConverter.class, com.dataracy.modules.common.config.web.WebMvcConfig.class}))
class UserCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserCommandWebMapper userCommandWebMapper;

    @MockBean
    private ModifyUserInfoUseCase modifyUserInfoUseCase;

    @MockBean
    private WithdrawUserUseCase withdrawUserUseCase;

    @MockBean
    private LogoutUserUseCase logoutUserUseCase;

    @MockBean
    private CookieUtil cookieUtil;
    
    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;
    
    @MockBean
    private com.dataracy.modules.common.config.web.MultipartJackson2HttpMessageConverter multipartJackson2HttpMessageConverter;

    @BeforeEach
    void setupResolver() throws Exception {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new com.dataracy.modules.user.adapter.web.api.command.UseCommandController(
                        userCommandWebMapper,
                        modifyUserInfoUseCase,
                        withdrawUserUseCase,
                        logoutUserUseCase,
                        cookieUtil
                ))
                .setCustomArgumentResolvers(currentUserIdArgumentResolver)
                .build();

        // 모든 @CurrentUserId → userId=1L
        given(currentUserIdArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
    }

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("modifyUserInfo API: 성공 - 200 OK와 JSON 응답 검증")
    void modifyUserInfoSuccess() throws Exception {
        // given
        Long userId = 1L;
        MockMultipartFile profileImageFile = new MockMultipartFile(
                "profileImageFile", 
                "profile.jpg", 
                "image/jpeg", 
                "test image content".getBytes()
        );
        
        ModifyUserInfoWebRequest webRequest = new ModifyUserInfoWebRequest(
                "Updated",
                1L, // authorLevelId
                2L, // occupationId
                List.of(1L, 2L), // topicIds
                3L, // visitSourceId
                "Updated Introduction Text"
        );

        ModifyUserInfoRequest requestDto = new ModifyUserInfoRequest(
                "Updated",
                1L, // authorLevelId
                2L, // occupationId
                List.of(1L, 2L), // topicIds
                3L, // visitSourceId
                "Updated Introduction Text"
        );

        given(userCommandWebMapper.toApplicationDto(webRequest)).willReturn(requestDto);
        willDoNothing().given(modifyUserInfoUseCase).modifyUserInfo(userId, profileImageFile, requestDto);

        // when & then
        MockMultipartFile webRequestFile = new MockMultipartFile(
                "webRequest",
                "webRequest.json",
                "application/json",
                objectMapper.writeValueAsBytes(webRequest)
        );

        mockMvc.perform(multipart("/api/v1/user")
                        .file(profileImageFile)
                        .file(webRequestFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_MODIFY_USER_INFO.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_MODIFY_USER_INFO.getMessage()));

        then(userCommandWebMapper).should().toApplicationDto(webRequest);
        then(modifyUserInfoUseCase).should().modifyUserInfo(userId, profileImageFile, requestDto);
    }

    @Test
    @DisplayName("withdrawUser API: 성공 - 200 OK와 JSON 응답 검증")
    void withdrawUserSuccess() throws Exception {
        // given
        Long userId = 1L;

        willDoNothing().given(withdrawUserUseCase).withdrawUser(userId);

        // when & then
        mockMvc.perform(delete("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_WITHDRAW_USER.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_WITHDRAW_USER.getMessage()));

        then(withdrawUserUseCase).should().withdrawUser(userId);
    }

    @Test
    @DisplayName("logout API: 성공 - 200 OK와 JSON 응답 검증")
    void logoutSuccess() throws Exception {
        // given
        Long userId = 1L;
        String refreshToken = "refresh-token";

        willDoNothing().given(logoutUserUseCase).logout(userId, refreshToken);
        willDoNothing().given(cookieUtil).deleteAllAuthCookies(any(), any());

        // when & then
        mockMvc.perform(post("/api/v1/user/logout")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_LOGOUT.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_LOGOUT.getMessage()));

        then(logoutUserUseCase).should().logout(userId, refreshToken);
        then(cookieUtil).should().deleteAllAuthCookies(any(), any());
    }
}