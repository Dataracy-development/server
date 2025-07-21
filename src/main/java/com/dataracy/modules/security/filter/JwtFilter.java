package com.dataracy.modules.security.filter;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.security.principal.CustomUserDetails;
import com.dataracy.modules.security.principal.UserAuthentication;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증을 하는 커스텀 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtValidateUseCase jwtValidateUseCase;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws BusinessException, ServletException, IOException {
        try {
            // 헤더에서 어세스 토큰 추출
            String accessToken = ExtractHeaderUtil.extractAccessToken(request)
                    .orElseThrow(() -> new AuthException(AuthErrorStatus.NOT_FOUND_ACCESS_TOKEN_IN_HEADER));
            log.debug("Extracted JWT Token: {}", accessToken);

            // 어세스 토큰 유효성 검증
            jwtValidateUseCase.validateToken(accessToken);

            // 토큰에서 유저 id, 유저 역할 반환
            Long userId = jwtValidateUseCase.getUserIdFromToken(accessToken);
            RoleType role = jwtValidateUseCase.getRoleFromToken(accessToken);
            log.debug("Authenticated UserId: {}", userId);

            // 인증 객체 SecurityContextHolder에 주입
            setAuthentication(request, userId, role);
        }
        catch (BusinessException e) {
            // 예외를 요청에 저장
            request.setAttribute("filter.error", e);
            throw new AuthenticationCredentialsNotFoundException("JWT 인증 실패", e);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 인증이 필요 없는 특정 경로에 대해 JWT 필터 적용을 건너뜁니다.
     *
     * Swagger, API 문서, 정적 리소스, 웹훅, 로그인, OAuth2, 루트, 에러, 공개 API, 파비콘, 프로젝트 검색 등 인증 없이 접근 가능한 요청 경로에 대해 true를 반환합니다.
     *
     * @param request 현재 HTTP 요청 객체
     * @return 해당 요청 경로가 필터 예외 대상이면 true, 그렇지 않으면 false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
//        log.info("[JWT FILTER DEBUG] Request URI: {}", request.getRequestURI());
        return
                path.startsWith("/swagger") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-resources") || path.equals("/swagger-config")
                || path.startsWith("/webjars") || path.startsWith("/.well-known") || path.equals("/favicon.ico")
                || path.startsWith("/static") || path.equals("/webhook")
                || path.equals("/api/v1/base") || path.equals("/api/v1/onboarding")
                || path.startsWith("/login") || path.startsWith("/oauth2")
                || path.equals("/") || path.startsWith("/error")
                || path.startsWith("/api/v1/references")
                || path.startsWith("/api/v1/email")
                || path.startsWith("/api/v1/signup")
                || path.startsWith("/api/v1/auth")
                || path.equals("/api/v1/nickname/check")
                || path.startsWith("/api/v1/projects/search")  || (path.startsWith("/api/v1/projects") && request.getMethod().equals("GET"))
                ;
    }

    // 사용자 인증 설정
    private void setAuthentication(HttpServletRequest request, Long userId, RoleType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication authentication = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
