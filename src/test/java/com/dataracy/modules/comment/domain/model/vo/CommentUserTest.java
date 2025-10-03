/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.domain.model.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

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
        "intro text");
  }

  @Test
  @DisplayName("UserInfo로부터 CommentUser 변환")
  void fromUserInfo() {
    // given
    UserInfo info = dummyUserInfo();

    // when
    CommentUser user = CommentUser.fromUserInfo(info);

    // then
    assertAll(
        () -> assertThat(user.userId()).isEqualTo(1L),
        () -> assertThat(user.role()).isEqualTo(RoleType.ROLE_USER),
        () -> assertThat(user.nickname()).isEqualTo("nickname"),
        () -> assertThat(user.authorLevelId()).isEqualTo(2L));
  }
}
