package com.dataracy.modules.user.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;

class UserTopicEntityTest {

  private UserEntity user() {
    return UserEntity.of(
        ProviderType.LOCAL,
        "pid",
        RoleType.ROLE_USER,
        "u@test.com",
        "pw",
        "nick",
        1L,
        null,
        null,
        "img.png",
        "intro",
        true,
        false);
  }

  @Test
  @DisplayName("of: UserTopicEntity 생성 성공")
  void ofSuccess() {
    // given
    UserEntity user = user();

    // when
    UserTopicEntity topic = UserTopicEntity.of(user, 100L);

    // then
    assertAll(
        () -> assertThat(topic.getUser()).isEqualTo(user),
        () -> assertThat(topic.getTopicId()).isEqualTo(100L));
  }
}
