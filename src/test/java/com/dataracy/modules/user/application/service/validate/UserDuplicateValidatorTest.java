package com.dataracy.modules.user.application.service.validate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.application.port.out.validate.ValidateUserExistsPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

@ExtendWith(MockitoExtension.class)
class UserDuplicateValidatorTest {

  @Mock private ValidateUserExistsPort userExistencePort;
  @Mock private UserQueryPort userQueryPort;

  @InjectMocks private UserDuplicateValidator validator;

  @Test
  @DisplayName("duplicateNickname - 닉네임이 중복되지 않은 경우 예외가 발생하지 않는다")
  void duplicateNicknameWhenNicknameIsNotDuplicatedDoesNotThrowException() {
    // given
    String nickname = "testNickname";
    when(userExistencePort.existsByNickname(nickname)).thenReturn(false);

    // when & then
    validator.duplicateNickname(nickname);
  }

  @Test
  @DisplayName("duplicateNickname - 닉네임이 중복된 경우 UserException이 발생한다")
  void duplicateNicknameWhenNicknameIsDuplicatedThrowsUserException() {
    // given
    String nickname = "duplicateNickname";
    when(userExistencePort.existsByNickname(nickname)).thenReturn(true);

    // when & then
    assertThatThrownBy(() -> validator.duplicateNickname(nickname))
        .isInstanceOf(UserException.class)
        .hasFieldOrPropertyWithValue("errorCode", UserErrorStatus.DUPLICATED_NICKNAME);
  }

  @Test
  @DisplayName("duplicateEmail - 이메일이 중복되지 않은 경우 빈 Optional을 반환한다")
  void duplicateEmailWhenEmailIsNotDuplicatedReturnsEmptyOptional() {
    // given
    String email = "test@example.com";
    when(userQueryPort.findUserByEmail(email)).thenReturn(Optional.empty());

    // when
    Optional<User> result = validator.duplicateEmail(email);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("duplicateEmail - 이메일이 중복된 경우 해당 사용자를 포함한 Optional을 반환한다")
  void duplicateEmailWhenEmailIsDuplicatedReturnsOptionalWithUser() {
    // given
    String email = "duplicate@example.com";
    User existingUser = User.builder().email(email).build();
    when(userQueryPort.findUserByEmail(email)).thenReturn(Optional.of(existingUser));

    // when
    Optional<User> result = validator.duplicateEmail(email);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(existingUser);
  }
}
