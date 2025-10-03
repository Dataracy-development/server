/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.adapter.jpa.impl.query;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class UserQueryDbAdapterTest {

  @Mock private UserJpaRepository userJpaRepository;

  @InjectMocks private UserQueryDbAdapter adapter;

  private UserEntity entity() {
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
  @DisplayName("ID로 사용자 조회 성공")
  void findUserByIdSuccess() {
    given(userJpaRepository.findById(1L)).willReturn(Optional.of(entity()));

    Optional<User> result = adapter.findUserById(1L);

    assertAll(
        () -> assertThat(result).isPresent(),
        () -> assertThat(result.get().getNickname()).isEqualTo("nick"));
    then(userJpaRepository).should().findById(1L);
  }

  @Test
  @DisplayName("이메일로 사용자 조회 실패 - 존재하지 않음")
  void findUserByEmailEmpty() {
    given(userJpaRepository.findByEmail("x@test.com")).willReturn(Optional.empty());

    Optional<User> result = adapter.findUserByEmail("x@test.com");

    assertThat(result).isEmpty();
    then(userJpaRepository).should().findByEmail("x@test.com");
  }
}
