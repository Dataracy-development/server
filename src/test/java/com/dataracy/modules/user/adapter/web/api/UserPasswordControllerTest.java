package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.user.adapter.web.api.password.UserPasswordController;
import com.dataracy.modules.user.adapter.web.mapper.password.UserPasswordWebMapper;
import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ResetPasswordWithTokenWebRequest;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;
import com.dataracy.modules.user.application.port.in.command.password.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.in.query.password.ConfirmPasswordUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UserPasswordController.class)
class UserPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserPasswordWebMapper mapper;

    @MockBean
    private ChangePasswordUseCase changePasswordUseCase;

    @MockBean
    private ConfirmPasswordUseCase confirmPasswordUseCase;

    // 공통 모킹
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
    @DisplayName("changePassword: 성공 → 200 OK + 상태코드 검증")
    void changePasswordSuccess() throws Exception {
        Long userId = 10L;
        ChangePasswordWebRequest webReq = new ChangePasswordWebRequest("pw12345@", "pw12345@");
        ChangePasswordRequest reqDto = new ChangePasswordRequest("pw12345@", "pw12345@");

        given(mapper.toApplicationDto(any(ChangePasswordWebRequest.class))).willReturn(reqDto);
        willDoNothing().given(changePasswordUseCase).changePassword(eq(userId), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/api/v1/user/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_CHANGE_PASSWORD.getCode()));

        then(mapper).should().toApplicationDto(any(ChangePasswordWebRequest.class));
        then(changePasswordUseCase).should().changePassword(eq(userId), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("resetPasswordWithToken: 성공 → 200 OK + 상태코드 검증")
    void resetPasswordWithTokenSuccess() throws Exception {
        ResetPasswordWithTokenWebRequest webReq = new ResetPasswordWithTokenWebRequest("token", "pw12345@", "pw12345@");
        ResetPasswordWithTokenRequest reqDto = new ResetPasswordWithTokenRequest("token", "pw12345@", "pw12345@");

        given(mapper.toApplicationDto(any(ResetPasswordWithTokenWebRequest.class))).willReturn(reqDto);
        willDoNothing().given(changePasswordUseCase).resetPassword(any(ResetPasswordWithTokenRequest.class));

        mockMvc.perform(put("/api/v1/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_RESET_PASSWORD.getCode()));

        then(mapper).should().toApplicationDto(any(ResetPasswordWithTokenWebRequest.class));
        then(changePasswordUseCase).should().resetPassword(any(ResetPasswordWithTokenRequest.class));
    }

    @Test
    @DisplayName("confirmPassword: 성공 → 200 OK + 상태코드 검증")
    void confirmPasswordSuccess() throws Exception {
        Long userId = 77L;
        ConfirmPasswordWebRequest webReq = new ConfirmPasswordWebRequest("pw12345@");
        ConfirmPasswordRequest reqDto = new ConfirmPasswordRequest("pw12345@");

        given(mapper.toApplicationDto(any(ConfirmPasswordWebRequest.class))).willReturn(reqDto);
        willDoNothing().given(confirmPasswordUseCase).confirmPassword(eq(userId), any(ConfirmPasswordRequest.class));

        mockMvc.perform(post("/api/v1/user/password/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_CONFIRM_PASSWORD.getCode()));

        then(mapper).should().toApplicationDto(any(ConfirmPasswordWebRequest.class));
        then(confirmPasswordUseCase).should().confirmPassword(eq(userId), any(ConfirmPasswordRequest.class));
    }

    @Test
    @DisplayName("changePassword: 실패 → 내부 예외 발생 시 500 반환")
    void changePasswordFailure() throws Exception {
        Long userId = 10L;
        ChangePasswordWebRequest webReq = new ChangePasswordWebRequest("pw12345@", "pw12345@");
        ChangePasswordRequest reqDto = new ChangePasswordRequest("pw12345@", "pw12345@");

        given(mapper.toApplicationDto(any(ChangePasswordWebRequest.class))).willReturn(reqDto);
        willThrow(new RuntimeException("bad"))
                .given(changePasswordUseCase).changePassword(eq(userId), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/api/v1/user/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webReq)))
                .andExpect(status().is5xxServerError());

        then(mapper).should().toApplicationDto(any(ChangePasswordWebRequest.class));
        then(changePasswordUseCase).should().changePassword(eq(userId), any(ChangePasswordRequest.class));
    }
}
