package com.dataracy.modules.auth.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record AuthUser(
        Long userId,
        RoleType role,
        String email,
        String nickname
) {
    public static AuthUser from(UserInfo info) {
        return new AuthUser(
                info.id(),
                info.role(),
                info.email(),
                info.nickname()
        );
    }
}
