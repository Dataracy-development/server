package com.dataracy.modules.project.domain.model.vo;

import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProjectUserTest {

    @Test
    @DisplayName("fromUserInfo - 정상적으로 ProjectUser 생성")
    void fromUserInfoSuccess() {
        // given
        UserInfo info = new UserInfo(
                1L,
                RoleType.ROLE_USER,
                "test@email.com",
                "tester",
                2L,
                3L,
                List.of(10L, 20L),
                4L,
                "profile.png",
                "intro"
        );

        // when
        ProjectUser user = ProjectUser.fromUserInfo(info);

        // then
        assertThat(user.userId()).isEqualTo(1L);
        assertThat(user.nickname()).isEqualTo("tester");
        assertThat(user.role()).isEqualTo(RoleType.ROLE_USER);
    }

    @Test
    @DisplayName("fromUserInfo - null 입력 시 예외 발생")
    void fromUserInfoShouldThrowWhenNull() {
        // when
        ProjectException ex = catchThrowableOfType(() -> ProjectUser.fromUserInfo(null), ProjectException.class);

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.FAIL_GET_USER_INFO);
    }
}
