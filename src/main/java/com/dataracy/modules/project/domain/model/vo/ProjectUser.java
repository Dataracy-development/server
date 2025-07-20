package com.dataracy.modules.project.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record ProjectUser(
        Long userId,
        RoleType role,
        String email,
        String nickname,
        Long occupationId
) {
    /**
     * 주어진 UserInfo 객체로부터 ProjectUser 인스턴스를 생성합니다.
     *
     * @param info ProjectUser로 변환할 사용자 정보 객체
     * @return UserInfo의 정보를 기반으로 생성된 ProjectUser 인스턴스
     */
    public static ProjectUser from(UserInfo info) {
        return new ProjectUser(
                info.id(),
                info.role(),
                info.email(),
                info.nickname(),
                info.occupationId()
        );
    }
}
