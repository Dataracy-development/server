package com.dataracy.modules.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum RoleStatusType {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_ANONYMOUS("ROLE_ANONYMOUS");

    private final String role;

    public static Optional<RoleStatusType> of(String role) {
        return Arrays.stream(RoleStatusType.values())
                .filter(status -> status.getRole().equalsIgnoreCase(role))
                .findFirst();
    }
}
