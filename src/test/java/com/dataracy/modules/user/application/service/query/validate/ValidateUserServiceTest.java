/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.service.query.validate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.user.application.service.validate.UserDuplicateValidator;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

@ExtendWith(MockitoExtension.class)
class ValidateUserServiceTest {

  @Mock private UserDuplicateValidator userDuplicateValidator;

  @InjectMocks private ValidateUserService service;

  private User userWithProvider(ProviderType provider) {
    return User.builder()
        .id(1L)
        .provider(provider)
        .providerId("pid")
        .email("user@test.com")
        .password("encoded")
        .nickname("nick")
        .topicIds(Collections.emptyList())
        .isDeleted(false)
        .build();
  }

  // ---------------- validateDuplicatedNickname ----------------
  @Test
  @DisplayName("닉네임 중복 없음 → 성공 (예외 없음)")
  void validateDuplicatedNicknameSuccess() {
    // given
    willDoNothing().given(userDuplicateValidator).duplicateNickname("nick");

    // when & then
    assertThatCode(() -> service.validateDuplicatedNickname("nick")).doesNotThrowAnyException();
  }

  // ---------------- validateDuplicatedEmail ----------------
  @Nested
  class ValidateDuplicatedEmailTests {

    @Test
    @DisplayName("구글 계정 존재 → DUPLICATED_GOOGLE_EMAIL 예외")
    void validateDuplicatedEmailGoogle() {
      // given
      given(userDuplicateValidator.duplicateEmail("google@test.com"))
          .willReturn(Optional.of(userWithProvider(ProviderType.GOOGLE)));

      // when
      UserException ex =
          catchThrowableOfType(
              () -> service.validateDuplicatedEmail("google@test.com"), UserException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
    }

    @Test
    @DisplayName("카카오 계정 존재 → DUPLICATED_KAKAO_EMAIL 예외")
    void validateDuplicatedEmailKakao() {
      // given
      given(userDuplicateValidator.duplicateEmail("kakao@test.com"))
          .willReturn(Optional.of(userWithProvider(ProviderType.KAKAO)));

      // when
      UserException ex =
          catchThrowableOfType(
              () -> service.validateDuplicatedEmail("kakao@test.com"), UserException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
    }

    @Test
    @DisplayName("로컬 계정 존재 → DUPLICATED_LOCAL_EMAIL 예외")
    void validateDuplicatedEmailLocal() {
      // given
      given(userDuplicateValidator.duplicateEmail("local@test.com"))
          .willReturn(Optional.of(userWithProvider(ProviderType.LOCAL)));

      // when
      UserException ex =
          catchThrowableOfType(
              () -> service.validateDuplicatedEmail("local@test.com"), UserException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
    }

    @Test
    @DisplayName("알 수 없는 ProviderType → DUPLICATED_LOCAL_EMAIL 예외 처리")
    void validateDuplicatedEmailUnknownProvider() {
      // given
      User user = userWithProvider(null); // 강제로 null Provider
      given(userDuplicateValidator.duplicateEmail("unknown@test.com"))
          .willReturn(Optional.of(user));

      // when
      UserException ex =
          catchThrowableOfType(
              () -> service.validateDuplicatedEmail("unknown@test.com"), UserException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
    }

    @Test
    @DisplayName("중복 이메일 없음 → 성공 (예외 없음)")
    void validateDuplicatedEmailSuccess() {
      // given
      given(userDuplicateValidator.duplicateEmail("free@test.com")).willReturn(Optional.empty());

      // when & then
      assertThatCode(() -> service.validateDuplicatedEmail("free@test.com"))
          .doesNotThrowAnyException();
    }
  }
}
