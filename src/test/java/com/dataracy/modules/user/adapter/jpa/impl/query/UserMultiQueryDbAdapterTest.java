package com.dataracy.modules.user.adapter.jpa.impl.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;

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

@ExtendWith(MockitoExtension.class)
class UserMultiQueryDbAdapterTest {

  @Mock private UserJpaRepository userJpaRepository;

  @InjectMocks private UserMultiQueryDbAdapter adapter;

  private UserEntity entity() {
    return UserEntity.of(
        ProviderType.LOCAL,
        "pid",
        RoleType.ROLE_USER,
        "u@test.com",
        "pw",
        "nick",
        null,
        null,
        null,
        null,
        "intro",
        true,
        false);
  }

  @Test
  @DisplayName("사용자 이름 조회 성공")
  void findUsernamesByIdsSuccess() {
    given(userJpaRepository.findAllById(anyList())).willReturn(List.of(entity()));

    Map<Long, String> map = adapter.findUsernamesByIds(List.of(1L));

    assertThat(map).isNotEmpty();
    then(userJpaRepository).should().findAllById(anyList());
  }

  @Test
  @DisplayName("썸네일 조회 - null일 경우 빈 문자열 반환")
  void findUserThumbnailsByIdsNullImage() {
    UserEntity e = entity();
    given(userJpaRepository.findAllById(anyList())).willReturn(List.of(e));

    Map<Long, String> map = adapter.findUserThumbnailsByIds(List.of(1L));

    assertThat(map).containsValue("");
    then(userJpaRepository).should().findAllById(anyList());
  }

  @Test
  @DisplayName("작성자 레벨 조회 - null이면 기본값 1 반환")
  void findUserAuthorLevelIdsNullDefault() {
    UserEntity e = entity();
    given(userJpaRepository.findAllById(anyList())).willReturn(List.of(e));

    Map<Long, String> map = adapter.findUserAuthorLevelIds(List.of(1L));

    assertThat(map).containsValue("1");
    then(userJpaRepository).should().findAllById(anyList());
  }
}
