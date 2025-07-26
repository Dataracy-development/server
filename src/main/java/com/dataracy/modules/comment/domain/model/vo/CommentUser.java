package com.dataracy.modules.comment.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record CommentUser(
        Long userId,
        RoleType role,
        String nickname,
        Long authorLevelId
) {
    public static CommentUser from(UserInfo info) {
        return new CommentUser(
                info.id(),
                info.role(),
                info.nickname(),
                info.authorLevelId()
        );
    }
}
