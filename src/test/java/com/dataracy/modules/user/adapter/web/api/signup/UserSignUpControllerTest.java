
package com.dataracy.modules.user.adapter.web.api.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.mapper.signup.UserSignUpWebMapper;
import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.application.port.in.command.signup.OAuthSignUpUseCase;
import com.dataracy.modules.user.application.port.in.command.signup.SelfSignUpUseCase;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignUpControllerTest {

    @Mock SelfSignUpUseCase selfSignUpUseCase;
    @Mock OAuthSignUpUseCase oauthSignUpUseCase;
    @Mock UserSignUpWebMapper userSignUpWebMapper;
    @Mock HttpServletResponse servletResponse;

    @InjectMocks UserSignUpController controller;

    @Test
    @DisplayName("signUpSelf: 정상 플로우 → 201 CREATED + 쿠키 설정 시도")
    void signUpSelf_success() {
        // given: null 대신 실제 요청 객체 사용
        SelfSignUpWebRequest webRequest = new SelfSignUpWebRequest(
                "user@test.com", "pw12345@", "pw12345@", "nick", 1L, 2L, List.of(10L), 3L, true
        );
        SelfSignUpRequest reqDto = new SelfSignUpRequest(
                "user@test.com", "pw12345@", "pw12345@", "nick", 1L, 2L, List.of(10L), 3L, true
        );

        given(userSignUpWebMapper.toApplicationDto(any(SelfSignUpWebRequest.class)))
                .willReturn(reqDto);
        given(selfSignUpUseCase.signUpSelf(any(SelfSignUpRequest.class)))
                .willReturn(new RefreshTokenResponse("refresh-token", Instant.now().toEpochMilli() + 3_600_000));

        // when
        ResponseEntity<SuccessResponse<Void>> res = controller.signUpUserSelf(webRequest, servletResponse);

        // then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(userSignUpWebMapper).should().toApplicationDto(any(SelfSignUpWebRequest.class));
        then(selfSignUpUseCase).should().signUpSelf(argThat(r ->
                r.email().equals("user@test.com") && r.nickname().equals("nick")));
    }

    @Test
    @DisplayName("signUpOAuth: 정상 플로우 → 201 CREATED + 쿠키 설정 시도")
    void signUpOAuth_success() {
        String registerToken = "reg-token";
        OnboardingWebRequest webRequest = new OnboardingWebRequest(
                "nick", 1L, 2L, List.of(10L, 20L), 3L, true
        );
        OnboardingRequest reqDto = new OnboardingRequest(
                "nick", 1L, 2L, List.of(10L, 20L), 3L, true
        );

        given(userSignUpWebMapper.toApplicationDto(any(OnboardingWebRequest.class)))
                .willReturn(reqDto);
        given(oauthSignUpUseCase.signUpOAuth(eq(registerToken), any(OnboardingRequest.class)))
                .willReturn(new RefreshTokenResponse("refresh-token", Instant.now().toEpochMilli() + 3_600_000));

        ResponseEntity<SuccessResponse<Void>> res = controller.signUpUserOAuth(registerToken, webRequest, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(userSignUpWebMapper).should().toApplicationDto(any(OnboardingWebRequest.class));
        then(oauthSignUpUseCase).should().signUpOAuth(eq(registerToken), argThat(r ->
                r.nickname().equals("nick") && r.occupationId().equals(2L)));
    }

    @Test
    @DisplayName("signUpSelf: UseCase 예외 발생 시 → 예외 전파")
    void signUpSelf_fail_propagates() {
        SelfSignUpWebRequest webRequest = new SelfSignUpWebRequest(
                "dup@test.com", "pw12345@", "pw12345@", "nick", 1L, null, null, null, false
        );
        SelfSignUpRequest reqDto = new SelfSignUpRequest(
                "dup@test.com", "pw12345@", "pw12345@", "nick", 1L, null, null, null, false
        );

        given(userSignUpWebMapper.toApplicationDto(any(SelfSignUpWebRequest.class)))
                .willReturn(reqDto);
        willThrow(new RuntimeException("boom"))
                .given(selfSignUpUseCase).signUpSelf(any(SelfSignUpRequest.class));

        Throwable ex = catchThrowable(() -> controller.signUpUserSelf(webRequest, servletResponse));
        assertThat(ex).isInstanceOf(RuntimeException.class).hasMessageContaining("boom");
    }
}
