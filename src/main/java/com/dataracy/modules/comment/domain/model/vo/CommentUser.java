package com.dataracy.modules.comment.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record CommentUser(
        Long userId,
        RoleType role,
        String nickname,
        Long authorLevelId
) {
    /**
     * 주어진 UserInfo 객체에서 정보를 추출하여 CommentUser 인스턴스를 생성합니다.
     *
     * @param info CommentUser로 변환할 사용자 정보 객체
     * @return UserInfo의 필드를 기반으로 생성된 CommentUser 인스턴스
     */
    public static CommentUser fromUserInfo(UserInfo info) {
        return new CommentUser(
                info.id(),
                info.role(),
                info.nickname(),
                info.authorLevelId()
        );
    }
}
