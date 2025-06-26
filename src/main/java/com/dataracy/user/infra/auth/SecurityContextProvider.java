package com.dataracy.user.infra.auth;

import com.dataracy.user.domain.enums.RoleStatusType;
import com.dataracy.user.status.AuthErrorStatus;
import com.dataracy.user.status.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityContextProvider {

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[SecurityContextProvider] No authenticated user found.");
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            log.warn("[SecurityContextProvider] Principal is not CustomUserDetails. Actual type: {}", principal.getClass().getName());
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }

        return customUserDetails.getUserId();
    }

    public static RoleStatusType getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[SecurityContextProvider] No authenticated user found.");
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            log.warn("[SecurityContextProvider] Principal is not CustomUserDetails. Actual type: {}", principal.getClass().getName());
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }

        return customUserDetails.getRole();
    }

    // 테스트용: SecurityContext에 CustomUserDetails 기반 토큰 주입
    public static void setupSecurityContextForTest(Long userId, RoleStatusType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication authentication = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 테스트 후 SecurityContext 클리어
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
