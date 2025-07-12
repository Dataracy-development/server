package com.dataracy.modules.security.principal;

import com.dataracy.modules.security.status.SecurityErrorStatus;
import com.dataracy.modules.security.exception.SecurityException;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 시스템 내 인증된 사용자 표현을 위한 클래스입니다.
 */
public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    /**
     * 현재 로그인한 유저의 식별자 반환
     */
    public Long getUserId() {
        if (getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new SecurityException(SecurityErrorStatus.UNEXPECTED_PRINCIPAL_TYPE_NOT_USER_DETAILS);
    }

    /**
     * 유저 닉네임 반환
     */
    public String getNickname() {
        if (getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername();
        }
        return "unknown";
    }

    /**
     * 유저 역할 반환
     */
    public RoleType getRole() {
        if (getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getRole();
        }
        return RoleType.ROLE_ANONYMOUS;
    }

    /**
     * 어드민 여부 확인 (권한 기반)
     */
    public boolean isAdmin() {
        return getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
