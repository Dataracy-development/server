package com.dataracy.user.infra.security;

import com.dataracy.user.domain.enums.RoleStatusType;
import com.dataracy.user.infra.anonymous.AnonymousUser;
import com.dataracy.user.status.AuthErrorStatus;
import com.dataracy.user.status.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityContextProvider {

    public static boolean isAnonymous() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal() instanceof AnonymousUser;
    }

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth!=null && auth.getPrincipal() instanceof CustomUserDetails;
    }

    public static Long getAuthenticatedUserId() {
        if (!isAuthenticated()) {
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUserId();
    }

    public static RoleStatusType getAuthenticatedUserRole() {
        if (!isAuthenticated()) {
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getRole();
    }

    public static String getAnonymousId(HttpServletRequest request) {
        if (isAnonymous()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AnonymousUser anonymousUser = (AnonymousUser) auth.getPrincipal();
            return anonymousUser.getAnonymousId();
        }
        // fallback: 필터에서 등록된 attribute
        return (String) request.getAttribute("anonymousId");
    }

    // 테스트용: SecurityContext에 CustomUserDetails 기반 토큰 주입
    public static void setupSecurityContextForTest(Long userId, RoleStatusType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication auth = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 테스트 후 SecurityContext 클리어
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
