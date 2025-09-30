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
     * Utility 클래스이므로 인스턴스화를 방지합니다.
     */
    private SecurityContextProvider() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 인증 객체 추출
     *
     * @return 인증 객체
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 익명 유저인지 여부 확인
     *
     * @return 추출한 인증 객체가 익명 유저인지 확인한다.
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
     * 인증되지 않은 경우 AuthException이 발생합니다.
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
     * 현재 인증된 사용자의 역할을 반환합니다.
     * 인증되지 않은 경우 AuthException이 발생합니다.
     *
     * @return 인증된 사용자의 역할
     * @throws AuthException 사용자가 인증되지 않은 경우 발생
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
     * 익명 유저 아이디를 반환한다.
     *
     * @param request Http 요청 객체
     * @return 익명 아이디
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
     * 테스트를 위한 인증 객체를 추가한다.
     *
     * @param userId 유저 아이디
     * @param role 유저 역할
     */
    public static void setupSecurityContextForTest(Long userId, RoleType role) {
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        UserAuthentication auth = new UserAuthentication(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * 시큐리티 컨텍스트 초기화
     */
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
