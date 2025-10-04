package com.dataracy.modules.dataset.domain.model.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

class DataUserTest {

  @Test
  @DisplayName("fromUserInfo - UserInfo로부터 DataUser를 생성한다")
  void fromUserInfoWhenValidUserInfoCreatesDataUser() {
    // given
    UserInfo userInfo =
        new UserInfo(
            1L,
            RoleType.ROLE_USER,
            "test@example.com",
            "TestUser",
            1L,
            2L,
            List.of(1L, 2L),
            3L,
            "http://example.com/profile.jpg",
            "Hello, I am a test user.");

    // when
    DataUser result = DataUser.fromUserInfo(userInfo);

    // then
    assertAll(
        () -> assertThat(result.userId()).isEqualTo(1L),
        () -> assertThat(result.role()).isEqualTo(RoleType.ROLE_USER),
        () -> assertThat(result.email()).isEqualTo("test@example.com"),
        () -> assertThat(result.nickname()).isEqualTo("TestUser"),
        () -> assertThat(result.profileImageUrl()).isEqualTo("http://example.com/profile.jpg"),
        () -> assertThat(result.introductionText()).isEqualTo("Hello, I am a test user."),
        () -> assertThat(result.occupationId()).isEqualTo(2L),
        () -> assertThat(result.authorLevelId()).isEqualTo(1L));
  }

  @Test
  @DisplayName("fromUserInfo - UserInfo가 null인 경우 DataException이 발생한다")
  void fromUserInfoWhenUserInfoIsNullThrowsDataException() {
    // when & then
    DataException exception =
        catchThrowableOfType(() -> DataUser.fromUserInfo(null), DataException.class);
    assertAll(
        () -> org.assertj.core.api.Assertions.assertThat(exception).isNotNull(),
        () ->
            org.assertj.core.api.Assertions.assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.FAIL_GET_USER_INFO));
  }

  @Test
  @DisplayName("fromUserInfo - UserInfo의 모든 필드가 null인 경우에도 DataUser를 생성한다")
  void fromUserInfoWhenUserInfoFieldsAreNullCreatesDataUser() {
    // given
    UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null);

    // when
    DataUser result = DataUser.fromUserInfo(userInfo);

    // then
    assertAll(
        () -> assertThat(result.userId()).isNull(),
        () -> assertThat(result.role()).isNull(),
        () -> assertThat(result.email()).isNull(),
        () -> assertThat(result.nickname()).isNull(),
        () -> assertThat(result.profileImageUrl()).isNull(),
        () -> assertThat(result.introductionText()).isNull(),
        () -> assertThat(result.occupationId()).isNull(),
        () -> assertThat(result.authorLevelId()).isNull());
  }

  @Test
  @DisplayName("fromUserInfo - UserInfo의 일부 필드만 있는 경우 DataUser를 생성한다")
  void fromUserInfoWhenUserInfoHasPartialFieldsCreatesDataUser() {
    // given
    UserInfo userInfo =
        new UserInfo(
            1L,
            RoleType.ROLE_ADMIN,
            "admin@example.com",
            "AdminUser",
            null,
            null,
            null,
            null,
            null,
            null);

    // when
    DataUser result = DataUser.fromUserInfo(userInfo);

    // then
    assertAll(
        () -> assertThat(result.userId()).isEqualTo(1L),
        () -> assertThat(result.role()).isEqualTo(RoleType.ROLE_ADMIN),
        () -> assertThat(result.email()).isEqualTo("admin@example.com"),
        () -> assertThat(result.nickname()).isEqualTo("AdminUser"),
        () -> assertThat(result.profileImageUrl()).isNull(),
        () -> assertThat(result.introductionText()).isNull(),
        () -> assertThat(result.occupationId()).isNull(),
        () -> assertThat(result.authorLevelId()).isNull());
  }
}
