package com.dataracy.modules.user.adapter.jpa.impl.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserCommandDbAdapterTest {

  @Mock private UserJpaRepository userJpaRepository;

  @InjectMocks private UserCommandDbAdapter adapter;

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

  private User domain() {
    return User.of(
        1L,
        ProviderType.LOCAL,
        "pid",
        RoleType.ROLE_USER,
        "u@test.com",
        "pw",
        "nick",
        1L,
        null,
        null,
        null,
        "img.png",
        "intro",
        true,
        false);
  }

  @Test
  @DisplayName("사용자 저장 성공")
  void saveUserSuccess() {
    given(userJpaRepository.save(any(UserEntity.class))).willReturn(entity());

    User result = adapter.saveUser(domain());

    assertThat(result.getEmail()).isEqualTo("u@test.com");
    then(userJpaRepository).should().save(any(UserEntity.class));
  }

  @Test
  @DisplayName("비밀번호 변경 성공")
  void changePasswordSuccess() {
    UserEntity e = entity();
    given(userJpaRepository.findById(1L)).willReturn(Optional.of(e));

    adapter.changePassword(1L, "new");

    assertThat(e.getPassword()).isEqualTo("new");
    then(userJpaRepository).should().findById(1L);
  }

  @Test
  @DisplayName("비밀번호 변경 실패 - 사용자 없음")
  void changePasswordNotFound() {
    given(userJpaRepository.findById(1L)).willReturn(Optional.empty());

    UserException ex =
        catchThrowableOfType(() -> adapter.changePassword(1L, "x"), UserException.class);

    assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    then(userJpaRepository).should().findById(1L);
  }

  @Test
  @DisplayName("성공: 기존 유저의 프로필 이미지를 새로운 값으로 변경 후 저장")
  void updateProfileImageFile() {
    // given
    Long userId = 1L;
    UserEntity user = entity();
    given(userJpaRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    adapter.updateProfileImageFile(userId, "new.png");

    // then
    assertThat(user.getProfileImageUrl()).isEqualTo("new.png");
    then(userJpaRepository).should().save(user);
  }

  @Test
  @DisplayName("실패: 존재하지 않는 userId로 조회 시 UserException 발생")
  void failUserNotFound() {
    // given
    Long userId = 99L;
    given(userJpaRepository.findById(userId)).willReturn(Optional.empty());

    // when
    Throwable thrown = catchThrowable(() -> adapter.updateProfileImageFile(userId, "new.png"));

    // then
    assertThat(thrown)
        .isInstanceOf(UserException.class)
        .hasMessageContaining(UserErrorStatus.NOT_FOUND_USER.getMessage());
    then(userJpaRepository).should(never()).save(any());
  }

  @Test
  @DisplayName("회원 탈퇴 성공")
  void withdrawalUserSuccess() {
    adapter.withdrawalUser(1L);

    then(userJpaRepository).should().withdrawalUser(1L);
  }
}
