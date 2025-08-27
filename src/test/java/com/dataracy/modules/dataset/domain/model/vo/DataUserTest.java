package com.dataracy.modules.dataset.domain.model.vo;

import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DataUserTest {

    @Test
    @DisplayName("UserInfo → DataUser 매핑 시 필드가 올바르게 매핑된다")
    void fromUserInfoShouldMapCorrectly() {
        // given
        UserInfo userInfo = new UserInfo(
                1L,
                RoleType.ROLE_USER,
                "test@test.com",
                "nickname",
                1L,
                1L,
                List.of(1L, 2L),
                1L,
                "img.png",
                "intro"
        );

        // when
        DataUser dataUser = DataUser.fromUserInfo(userInfo);

        // then
        assertThat(dataUser.userId()).isEqualTo(1L);
        assertThat(dataUser.role()).isEqualTo(RoleType.ROLE_USER);
        assertThat(dataUser.email()).isEqualTo("test@test.com");
        assertThat(dataUser.nickname()).isEqualTo("nickname");
        assertThat(dataUser.profileImageUrl()).isEqualTo("img.png");
        assertThat(dataUser.introductionText()).isEqualTo("intro");
        assertThat(dataUser.occupationId()).isEqualTo(1L);
        assertThat(dataUser.authorLevelId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("UserInfo가 null일 경우 DataException(FAIL_GET_USER_INFO)을 던진다")
    void fromUserInfoShouldThrowWhenNull() {
        // given & when
        DataException exception = catchThrowableOfType(
                () -> DataUser.fromUserInfo(null),
                DataException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(DataErrorStatus.FAIL_GET_USER_INFO);
    }
}
