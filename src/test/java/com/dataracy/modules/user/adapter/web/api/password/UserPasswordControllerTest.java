
package com.dataracy.modules.user.adapter.web.api.password;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.mapper.password.UserPasswordWebMapper;
import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ResetPasswordWithTokenWebRequest;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;
import com.dataracy.modules.user.application.port.in.command.password.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.in.query.password.ConfirmPasswordUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserPasswordControllerTest {

    @Mock UserPasswordWebMapper mapper;
    @Mock ChangePasswordUseCase changePasswordUseCase;
    @Mock ConfirmPasswordUseCase confirmPasswordUseCase;

    @InjectMocks UserPasswordController controller;

    @Test
    @DisplayName("changePassword: 정상 플로우 → 200 OK")
    void changePassword_success() {
        Long userId = 10L;

        // null 대신 실제 요청 객체
        ChangePasswordWebRequest webRequest = new ChangePasswordWebRequest("pw12345@", "pw12345@");
        ChangePasswordRequest reqDto = new ChangePasswordRequest("pw12345@", "pw12345@");

        given(mapper.toApplicationDto(any(ChangePasswordWebRequest.class))).willReturn(reqDto);
        // 유즈케이스는 동일 인스턴스 보장 안 되므로 any(...)
        willDoNothing().given(changePasswordUseCase).changePassword(eq(userId), any(ChangePasswordRequest.class));

        ResponseEntity<SuccessResponse<Void>> res = controller.changePassword(userId, webRequest);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(mapper).should().toApplicationDto(any(ChangePasswordWebRequest.class));
        then(changePasswordUseCase).should().changePassword(eq(userId), argThat(r ->
                r.password().equals("pw12345@") && r.passwordConfirm().equals("pw12345@")));
    }

    @Test
    @DisplayName("resetPassword: 정상 플로우 → 200 OK")
    void resetPassword_success() {
        ResetPasswordWithTokenWebRequest webRequest = new ResetPasswordWithTokenWebRequest("token", "pw12345@", "pw12345@");
        ResetPasswordWithTokenRequest reqDto = new ResetPasswordWithTokenRequest("token", "pw12345@", "pw12345@");

        given(mapper.toApplicationDto(any(ResetPasswordWithTokenWebRequest.class))).willReturn(reqDto);
        willDoNothing().given(changePasswordUseCase).resetPassword(any(ResetPasswordWithTokenRequest.class));

        ResponseEntity<SuccessResponse<Void>> res = controller.resetPasswordWithToken(webRequest);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(mapper).should().toApplicationDto(any(ResetPasswordWithTokenWebRequest.class));
        then(changePasswordUseCase).should().resetPassword(argThat(r ->
                r.resetPasswordToken().equals("token")));
    }

    @Test
    @DisplayName("confirmPassword: 정상 플로우 → 200 OK")
    void confirmPassword_success() {
        Long userId = 77L;

        ConfirmPasswordWebRequest webRequest = new ConfirmPasswordWebRequest("pw12345@");
        ConfirmPasswordRequest reqDto = new ConfirmPasswordRequest("pw12345@");

        given(mapper.toApplicationDto(any(ConfirmPasswordWebRequest.class))).willReturn(reqDto);
        willDoNothing().given(confirmPasswordUseCase).confirmPassword(eq(userId), any(ConfirmPasswordRequest.class));

        ResponseEntity<SuccessResponse<Void>> res = controller.confirmPassword(userId, webRequest);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(mapper).should().toApplicationDto(any(ConfirmPasswordWebRequest.class));
        then(confirmPasswordUseCase).should().confirmPassword(eq(userId), argThat(r -> r.password().equals("pw12345@")));
    }

    @Test
    @DisplayName("changePassword: 유즈케이스 예외 → 전파")
    void changePassword_fail_propagates() {
        Long userId = 10L;

        ChangePasswordWebRequest webRequest = new ChangePasswordWebRequest("pw12345@", "pw12345@");
        ChangePasswordRequest reqDto = new ChangePasswordRequest("pw12345@", "pw12345@");

        given(mapper.toApplicationDto(any(ChangePasswordWebRequest.class))).willReturn(reqDto);
        willThrow(new RuntimeException("bad"))
                .given(changePasswordUseCase).changePassword(eq(userId), any(ChangePasswordRequest.class));

        Throwable ex = catchThrowable(() -> controller.changePassword(userId, webRequest));
        assertThat(ex).isInstanceOf(RuntimeException.class).hasMessageContaining("bad");
    }
}
