package com.dataracy.user.infra.auth.security;

import com.dataracy.user.domain.enums.RoleStatusType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final RoleStatusType role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role::getRole);
    }

    public Long getUserId() { return userId; }

    public RoleStatusType getRole() { return role; }

    @Override
    public String getPassword() { return null; }

    @Override
    public String getUsername() { return null; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
