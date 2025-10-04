package com.dataracy.modules.user.adapter.jpa.impl.validate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckUserExistsDbAdapterTest {

  @Mock private UserJpaRepository userJpaRepository;

  @InjectMocks private CheckUserExistsDbAdapter adapter;

  @Test
  @DisplayName("닉네임 존재 여부 true")
  void existsByNicknameTrue() {
    given(userJpaRepository.existsByNickname("nick")).willReturn(true);

    boolean result = adapter.existsByNickname("nick");

    assertThat(result).isTrue();
    then(userJpaRepository).should().existsByNickname("nick");
  }

  @Test
  @DisplayName("이메일 존재 여부 false")
  void existsByEmailFalse() {
    given(userJpaRepository.existsByEmail("x@test.com")).willReturn(false);

    boolean result = adapter.existsByEmail("x@test.com");

    assertThat(result).isFalse();
    then(userJpaRepository).should().existsByEmail("x@test.com");
  }
}
