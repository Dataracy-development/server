package com.dataracy.modules.user.application.service.query.validate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
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

  @Test
  @DisplayName("validateDuplicatedNickname - 닉네임이 중복되지 않은 경우 예외가 발생하지 않는다")
  void validateDuplicatedNicknameWhenNicknameIsNotDuplicatedDoesNotThrowException() {
    // given
    String nickname = "testNickname";

    // when & then - 예외가 발생하지 않으면 성공
    service.validateDuplicatedNickname(nickname);
  }

  @Test
  @DisplayName("validateDuplicatedNickname - 닉네임이 중복된 경우 UserException이 발생한다")
  void validateDuplicatedNicknameWhenNicknameIsDuplicatedThrowsUserException() {
    // given
    String nickname = "duplicateNickname";
    doThrow(new UserException(UserErrorStatus.DUPLICATED_NICKNAME))
        .when(userDuplicateValidator)
        .duplicateNickname(nickname);

    // when & then
    assertThatThrownBy(() -> service.validateDuplicatedNickname(nickname))
        .isInstanceOf(UserException.class)
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.DUPLICATED_NICKNAME);
  }

  @Test
  @DisplayName("validateDuplicatedEmail - 이메일이 중복되지 않은 경우 예외가 발생하지 않는다")
  void validateDuplicatedEmailWhenEmailIsNotDuplicatedDoesNotThrowException() {
    // given
    String email = "test@example.com";
    when(userDuplicateValidator.duplicateEmail(email)).thenReturn(Optional.empty());

    // when & then - 예외가 발생하지 않으면 성공
    service.validateDuplicatedEmail(email);
  }

  @Test
  @DisplayName("validateDuplicatedEmail - 구글 계정 이메일이 중복된 경우 DUPLICATED_GOOGLE_EMAIL 예외가 발생한다")
  void validateDuplicatedEmailWhenGoogleEmailIsDuplicatedThrowsGoogleEmailException() {
    // given
    String email = "google@example.com";
    User existingUser = User.builder().provider(ProviderType.GOOGLE).build();
    when(userDuplicateValidator.duplicateEmail(email)).thenReturn(Optional.of(existingUser));

    // when & then
    assertThatThrownBy(() -> service.validateDuplicatedEmail(email))
        .isInstanceOf(UserException.class)
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.DUPLICATED_GOOGLE_EMAIL);
  }

  @Test
  @DisplayName("validateDuplicatedEmail - 카카오 계정 이메일이 중복된 경우 DUPLICATED_KAKAO_EMAIL 예외가 발생한다")
  void validateDuplicatedEmailWhenKakaoEmailIsDuplicatedThrowsKakaoEmailException() {
    // given
    String email = "kakao@example.com";
    User existingUser = User.builder().provider(ProviderType.KAKAO).build();
    when(userDuplicateValidator.duplicateEmail(email)).thenReturn(Optional.of(existingUser));

    // when & then
    assertThatThrownBy(() -> service.validateDuplicatedEmail(email))
        .isInstanceOf(UserException.class)
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.DUPLICATED_KAKAO_EMAIL);
  }

  @Test
  @DisplayName("validateDuplicatedEmail - 로컬 계정 이메일이 중복된 경우 DUPLICATED_LOCAL_EMAIL 예외가 발생한다")
  void validateDuplicatedEmailWhenLocalEmailIsDuplicatedThrowsLocalEmailException() {
    // given
    String email = "local@example.com";
    User existingUser = User.builder().provider(ProviderType.LOCAL).build();
    when(userDuplicateValidator.duplicateEmail(email)).thenReturn(Optional.of(existingUser));

    // when & then
    assertThatThrownBy(() -> service.validateDuplicatedEmail(email))
        .isInstanceOf(UserException.class)
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
  }

  @Test
  @DisplayName("validateDuplicatedEmail - ProviderType이 null인 경우 DUPLICATED_LOCAL_EMAIL 예외가 발생한다")
  void validateDuplicatedEmailWhenProviderTypeIsNullThrowsLocalEmailException() {
    // given
    String email = "null@example.com";
    User existingUser = User.builder().provider(null).build();
    when(userDuplicateValidator.duplicateEmail(email)).thenReturn(Optional.of(existingUser));

    // when & then
    assertThatThrownBy(() -> service.validateDuplicatedEmail(email))
        .isInstanceOf(UserException.class)
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.DUPLICATED_LOCAL_EMAIL);
  }
}
