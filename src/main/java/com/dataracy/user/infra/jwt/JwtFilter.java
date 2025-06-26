package com.dataracy.user.infra.jwt;

import com.dataracy.common.exception.BusinessException;
import com.dataracy.common.util.ExtractHeaderUtil;
import com.dataracy.user.domain.enums.RoleStatusType;
import com.dataracy.user.infra.auth.CustomUserDetails;
import com.dataracy.user.infra.auth.UserAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws BusinessException, ServletException, IOException {
        try {
            ExtractHeaderUtil.extractAccessToken(request).ifPresent(accessToken -> {
                log.debug("Extracted JWT Token: {}", accessToken);

                jwtUtil.validateToken(accessToken); // 유효성 검증
                Long userId = jwtUtil.getUserIdFromToken(accessToken);
                RoleStatusType role = jwtUtil.getRoleFromToken(accessToken);
                log.debug("Authenticated UserId: {}", userId);

                setAuthentication(request, userId, role);
            });
            filterChain.doFilter(request, response);
        }
        catch (BusinessException e) {
            handleBusinessException(response, e);
            return;
        }
        catch (Exception e) {
            handleException(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 필터를 적용하지 않을 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")
                || path.equals("/") || path.equals("/base") || path.equals("/onboarding")
                || path.startsWith("/oauth2/callback")
                || path.startsWith("/static")
                || path.startsWith("/api/v1/auth")
                || path.startsWith("/api/v1/public")
                || path.equals("/api/v1/login") || path.equals("/api/v1/signup");
    }

    // 사용자 인증 설정
    private void setAuthentication(HttpServletRequest request, Long userId, RoleStatusType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication authentication = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 토큰 예외 처리 메서드
    private void handleBusinessException(HttpServletResponse response, BusinessException e) throws IOException {
        log.error("JWT 인증 예외 발생: {}", e.getMessage());
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"httpStatus\": \"%d\", \"code\": \"%s\", \"message\": \"%s\"}", e.getHttpStatus().value(), e.getCode(), e.getMessage());
        response.getWriter().write(jsonResponse);
    }

    // 토큰 예외 처리 메서드
    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        log.error("서버 예외 발생: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"httpStatus\": \"%d\", \"code\": \"%s\", \"message\": \"%s\"}", 500, "500", e.getMessage());
        response.getWriter().write(jsonResponse);
    }
}
