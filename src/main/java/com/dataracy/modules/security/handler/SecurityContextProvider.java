package com.dataracy.modules.security.handler;

import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.model.AnonymousUser;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.security.principal.CustomUserDetails;
import com.dataracy.modules.security.principal.UserAuthentication;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 인증객체에 대한 정보를 효율있게 제공하는 서비스
 */
@Slf4j
public class SecurityContextProvider {

    // 인증 객체 반환
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 익명 여부 반환
    public static boolean isAnonymous() {
        Authentication auth = getAuthentication();
        return auth.getPrincipal() instanceof AnonymousUser;
    }

    // 실제 로그인 한 유저인지 반환
    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth!=null && auth.getPrincipal() instanceof CustomUserDetails;
    }

    // 로그인 한 인증 유저의 id 반환 (비로그인일 경우 예외처리)
    public static Long getAuthenticatedUserId() {
        if (!isAuthenticated()) {
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }
        Authentication auth = getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUserId();
    }

    // 로그인 한 인증 유저의 id 반환 (비로그인일 경우 null 반환)
    public static Long getUserId() {
        if (!isAuthenticated()) {
            return null;
        }
        Authentication auth = getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUserId();
    }

    // 로그인 한 인증 유저의 역할 반환
    public static RoleType getAuthenticatedUserRole() {
        if (!isAuthenticated()) {
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }
        Authentication auth = getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getRole();
    }

    // 익명 유저 id 반환
    public static String getAnonymousId(HttpServletRequest request) {
        if (isAnonymous()) {
            Authentication auth = getAuthentication();
            AnonymousUser anonymousUser = (AnonymousUser) auth.getPrincipal();
            return anonymousUser.getAnonymousId();
        }
        // fallback: 필터에서 등록된 attribute
        return (String) request.getAttribute("anonymousId");
    }

    // 테스트용: SecurityContext에 CustomUserDetails 기반 토큰 주입
    public static void setupSecurityContextForTest(Long userId, RoleType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication auth = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 테스트 후 SecurityContext 클리어
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
