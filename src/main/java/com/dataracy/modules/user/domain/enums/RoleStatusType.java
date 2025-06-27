package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleStatusType {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_ANONYMOUS("ROLE_ANONYMOUS");

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
