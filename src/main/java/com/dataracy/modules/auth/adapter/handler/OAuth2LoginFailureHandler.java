package com.dataracy.modules.auth.adapter.handler;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 소셜 로그인 실패 핸들러
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    /**
     * 소셜 로그인 인증에 실패했을 때 호출되어 인증 실패를 처리합니다.
     *
     * 인증 실패 시 에러를 로깅한 후, 기본 인증 실패 처리 로직을 실행합니다.
     *
     * @param request 인증 요청을 포함하는 HTTP 요청 객체
     * @param response 인증 실패 응답을 위한 HTTP 응답 객체
     * @param exception 인증 실패 원인을 담고 있는 예외 객체
     * @throws ServletException 서블릿 처리 중 예외가 발생한 경우
     * @throws IOException 입출력 오류가 발생한 경우
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException, IOException {
        LoggerFactory.common().logError("Authentication", "소셜 로그인 에러", exception);
        super.onAuthenticationFailure(request, response, exception);
    }
}
