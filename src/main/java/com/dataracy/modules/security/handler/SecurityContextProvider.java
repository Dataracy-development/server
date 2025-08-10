package com.dataracy.modules.security.handler;

import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.model.AnonymousUser;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.security.principal.CustomUserDetails;
import com.dataracy.modules.security.principal.UserAuthentication;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 인증객체에 대한 정보를 효율있게 제공하는 서비스
 */
public class SecurityContextProvider {

    /**
     *
     * @return
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     *
     * @return
     */
    public static boolean isAnonymous() {
        Authentication auth = getAuthentication();
        return auth.getPrincipal() instanceof AnonymousUser;
    }

    /**
     * 현재 인증 주체가 실제 로그인한 사용자(CustomUserDetails)인지 여부를 반환합니다.
     *
     * @return 로그인한 사용자인 경우 true, 아니면 false
     */
    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth!=null && auth.getPrincipal() instanceof CustomUserDetails;
    }

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * 인증 정보가 존재하고, principal이 CustomUserDetails인 경우 해당 사용자의 ID를 반환합니다.
     *
     * @return 인증된 사용자의 ID
     */
    private static Long extractUserId() {
        Authentication auth = getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUserId();
    }

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * 인증되지 않은 경우 {@code AuthException}이 발생합니다.
     *
     * @return 인증된 사용자의 ID
     * @throws AuthException 사용자가 인증되지 않은 경우 발생
     */
    public static Long getAuthenticatedUserId() {
        if (!isAuthenticated()) {
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }
        return extractUserId();
    }

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * 인증된 사용자가 없는 경우 null을 반환합니다.
     *
     * @return 인증된 사용자의 ID 또는 인증되지 않은 경우 null
     */
    public static Long getUserId() {
        if (!isAuthenticated()) {
            return null;
        }
        return extractUserId();
    }

    /**
     *
     * @return
     */
    public static RoleType getAuthenticatedUserRole() {
        if (!isAuthenticated()) {
            throw new AuthException(AuthErrorStatus.NOT_AUTHENTICATED);
        }
        Authentication auth = getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getRole();
    }

    /**
     *
     * @param request
     * @return
     */
    public static String getAnonymousId(HttpServletRequest request) {
        if (isAnonymous()) {
            Authentication auth = getAuthentication();
            AnonymousUser anonymousUser = (AnonymousUser) auth.getPrincipal();
            return anonymousUser.getAnonymousId();
        }
        // fallback: 필터에서 등록된 attribute
        return (String) request.getAttribute("anonymousId");
    }

    /**
     *
     * @param userId
     * @param role
     */
    public static void setupSecurityContextForTest(Long userId, RoleType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication auth = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     *
     */
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
