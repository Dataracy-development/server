package com.dataracy.modules.dataset.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record DataUser(
        Long userId,
        RoleType role,
        String email,
        String nickname,
        Long occupationId,
        Long authorLevelId
) {
    public static DataUser from(UserInfo info) {
        return new DataUser(
                info.id(),
                info.role(),
                info.email(),
                info.nickname(),
                info.occupationId(),
                info.authorLevelId()
        );
    }
}
