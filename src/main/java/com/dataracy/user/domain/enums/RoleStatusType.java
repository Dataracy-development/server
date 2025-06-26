package com.dataracy.user.domain.enums;

import com.dataracy.user.status.UserErrorStatus;
import com.dataracy.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleStatusType {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_ANONYMOUS("ANONYMOUS");

    private final String role;

    public static RoleStatusType of(String role) {
        for (RoleStatusType status : RoleStatusType.values()) {
            if (status.getRole().equalsIgnoreCase(role)) {
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_ROLE_STATUS_TYPE);
    }
}
