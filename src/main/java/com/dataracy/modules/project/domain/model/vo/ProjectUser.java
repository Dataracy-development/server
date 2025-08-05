package com.dataracy.modules.project.domain.model.vo;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public record ProjectUser(
        Long userId,
        RoleType role,
        String email,
        String nickname,
        Long occupationId,
        Long authorLevelId
) {
    /**
     * 주어진 UserInfo 객체로부터 ProjectUser 인스턴스를 생성합니다.
     *
     * info가 null인 경우 ProjectException이 발생합니다.
     *
     * @param info ProjectUser로 변환할 사용자 정보 객체
     * @return 변환된 ProjectUser 인스턴스
     */
    public static ProjectUser fromUserInfo(UserInfo info) {
        if (info == null) {
            LoggerFactory.domain().logWarning("ProjectUser을 생성하기 위한 유저 정보가 주입되지 않았습니다.");
            throw new ProjectException(ProjectErrorStatus.FAIL_GET_USER_INFO);
        }
        return new ProjectUser(
                info.id(),
                info.role(),
                info.email(),
                info.nickname(),
                info.occupationId(),
                info.authorLevelId()
        );
    }
}
