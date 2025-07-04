package com.dataracy.modules.security.filter;

import com.dataracy.modules.auth.application.JwtQueryService;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.security.principal.CustomUserDetails;
import com.dataracy.modules.security.principal.UserAuthentication;
import com.dataracy.modules.user.domain.enums.RoleStatusType;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtQueryService jwtQueryService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws BusinessException, ServletException, IOException {
        try {
            String accessToken = ExtractHeaderUtil.extractAccessToken(request)
                    .orElseThrow(() -> new AuthException(AuthErrorStatus.NOT_FOUND_ACCESS_TOKEN_IN_HEADER));
            log.debug("Extracted JWT Token: {}", accessToken);
            jwtQueryService.validateToken(accessToken); // 유효성 검증
            Long userId = jwtQueryService.getUserIdFromToken(accessToken);
            RoleStatusType role = jwtQueryService.getRoleFromToken(accessToken);
            log.debug("Authenticated UserId: {}", userId);
            setAuthentication(request, userId, role);
        }
        catch (BusinessException e) {
            request.setAttribute("filter.error", e); // 예외를 요청에 저장
            throw new AuthenticationCredentialsNotFoundException("JWT 인증 실패", e); // Spring Security 흐름 유지
        }
        filterChain.doFilter(request, response);
    }

    // 필터를 적용하지 않을 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")
                || path.equals("/") || path.equals("/api/v1/base") || path.equals("/api/v1/onboarding")
                || path.startsWith("/login")
                || path.startsWith("/.well-known")
                || path.startsWith("/oauth2")
                || path.startsWith("/static")
                || path.startsWith("/api/v1/auth")
                || path.startsWith("/api/v1/public")
                || path.equals("/api/v1/login") || path.equals("/api/v1/signup")
                || path.equals("/favicon.ico")
                || path.startsWith("/error")
                ;
    }

    // 사용자 인증 설정
    private void setAuthentication(HttpServletRequest request, Long userId, RoleStatusType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication authentication = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
