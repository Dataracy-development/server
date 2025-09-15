package com.dataracy.modules.auth.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record AuthUser(
        Long userId,
        RoleType role,
        String email,
        String nickname
) {
    /**
     * 주어진 UserInfo 객체로부터 AuthUser 인스턴스를 생성합니다.
     *
     * @param info AuthUser로 변환할 사용자 정보 객체
     * @return UserInfo의 정보를 기반으로 생성된 AuthUser 인스턴스
     */
    public static AuthUser from(UserInfo info) {
        return new AuthUser(
                info.id(),
                info.role(),
                info.email(),
                info.nickname()
        );
    }
}
