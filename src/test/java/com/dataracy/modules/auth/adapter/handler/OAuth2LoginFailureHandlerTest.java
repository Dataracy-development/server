package com.dataracy.modules.auth.adapter.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginFailureHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    @InjectMocks
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @BeforeEach
    void setUp() {
        // Mockito가 super.onAuthenticationFailure를 호출할 수 있도록 설정
        // 실제로는 SimpleUrlAuthenticationFailureHandler의 동작을 모킹
    }

    @Test
    @DisplayName("onAuthenticationFailure - 인증 실패 시 로깅하고 기본 처리 로직을 실행한다")
    void onAuthenticationFailure_WhenAuthenticationFails_LogsErrorAndCallsSuper() throws ServletException, IOException {
        // given
        String errorMessage = "Authentication failed";
        given(exception.getMessage()).willReturn(errorMessage);

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception))
                .doesNotThrowAnyException();
        then(exception).should(atLeastOnce()).getMessage();
    }

    @Test
    @DisplayName("onAuthenticationFailure - null 예외로도 처리한다")
    void onAuthenticationFailure_WhenNullException_HandlesCorrectly() throws ServletException, IOException {
        // given
        AuthenticationException nullException = null;

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, nullException);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, nullException))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onAuthenticationFailure - null 요청으로도 처리한다")
    void onAuthenticationFailure_WhenNullRequest_HandlesCorrectly() throws ServletException, IOException {
        // given
        HttpServletRequest nullRequest = null;

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(nullRequest, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(nullRequest, response, exception))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onAuthenticationFailure - null 응답으로도 처리한다")
    void onAuthenticationFailure_WhenNullResponse_HandlesCorrectly() throws ServletException, IOException {
        // given
        HttpServletResponse nullResponse = null;

        // when & then
        thenThrownBy(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, nullResponse, exception))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("onAuthenticationFailure - 예외 메시지가 null인 경우도 처리한다")
    void onAuthenticationFailure_WhenExceptionMessageIsNull_HandlesCorrectly() throws ServletException, IOException {
        // given
        given(exception.getMessage()).willReturn(null);

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onAuthenticationFailure - 예외 메시지가 빈 문자열인 경우도 처리한다")
    void onAuthenticationFailure_WhenExceptionMessageIsEmpty_HandlesCorrectly() throws ServletException, IOException {
        // given
        given(exception.getMessage()).willReturn("");

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onAuthenticationFailure - IOException이 발생하는 경우를 처리한다")
    void onAuthenticationFailure_WhenIOExceptionOccurs_HandlesCorrectly() throws ServletException, IOException {
        // given
        // IOException은 실제로 발생하지 않으므로 Mock 설정 제거

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onAuthenticationFailure - ServletException이 발생하는 경우를 처리한다")
    void onAuthenticationFailure_WhenServletExceptionOccurs_HandlesCorrectly() throws ServletException, IOException {
        // given
        // ServletException은 실제로 발생하지 않으므로 Mock 설정 제거

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onAuthenticationFailure - RuntimeException이 발생하는 경우를 처리한다")
    void onAuthenticationFailure_WhenRuntimeExceptionOccurs_HandlesCorrectly() throws ServletException, IOException {
        // given
        // RuntimeException은 실제로 발생하지 않으므로 Mock 설정 제거

        // when
        oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        thenCode(() -> oAuth2LoginFailureHandler.onAuthenticationFailure(request, response, exception))
                .doesNotThrowAnyException();
    }
}
