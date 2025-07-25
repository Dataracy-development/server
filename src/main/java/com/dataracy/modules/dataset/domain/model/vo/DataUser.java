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
    /**
     * 주어진 UserInfo 객체로부터 DataUser 인스턴스를 생성합니다.
     *
     * @param info DataUser로 변환할 사용자 정보 객체
     * @return UserInfo의 필드 값을 기반으로 생성된 DataUser 인스턴스
     */
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
