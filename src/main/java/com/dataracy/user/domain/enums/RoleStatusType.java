package com.dataracy.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum RoleStatusType {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_ANONYMOUS("ANONYMOUS");

    private final String role;

    public static Optional<RoleStatusType> of(String role) {
        return Arrays.stream(RoleStatusType.values())
                .filter(status -> status.getRole().equalsIgnoreCase(role))
                .findFirst();
    }
}
