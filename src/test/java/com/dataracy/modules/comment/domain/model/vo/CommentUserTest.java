package com.dataracy.modules.comment.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CommentUserTest {

    private UserInfo dummyUserInfo() {
        return new UserInfo(
                1L,
                RoleType.ROLE_USER,
                "test@example.com",
                "nickname",
                2L,
                3L,
                Collections.emptyList(),
                4L,
                "profile.png",
                "intro text"
        );
    }

    @Test
    @DisplayName("UserInfo로부터 CommentUser 변환")
    void fromUserInfo() {
        UserInfo info = dummyUserInfo();

        CommentUser user = CommentUser.fromUserInfo(info);

        assertThat(user.userId()).isEqualTo(1L);
        assertThat(user.role()).isEqualTo(RoleType.ROLE_USER);
        assertThat(user.nickname()).isEqualTo("nickname");
        assertThat(user.authorLevelId()).isEqualTo(2L);
    }
}
