/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.domain.exception;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dataracy.modules.user.domain.status.UserErrorStatus;

@DisplayName("UserException 테스트")
class UserExceptionTest {

  @Nested
  @DisplayName("예외 생성 테스트")
  class ExceptionCreationTest {

    @Test
    @DisplayName("성공: UserErrorStatus로 UserException을 생성한다")
    void success_createUserExceptionWithErrorStatus() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.NOT_FOUND_USER;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      assertThat(userException).isNotNull();
      assertAll(
          () -> assertThat(userException.getErrorCode()).isEqualTo(errorStatus),
          () -> assertThat(userException.getHttpStatus()).isEqualTo(errorStatus.getHttpStatus()),
          () -> assertThat(userException.getCode()).isEqualTo(errorStatus.getCode()),
          () -> assertThat(userException.getMessage()).isEqualTo(errorStatus.getMessage()));
    }

    @Test
    @DisplayName("성공: 다양한 UserErrorStatus로 UserException을 생성한다")
    void success_createUserExceptionWithVariousErrorStatuses() {
      // given & when & then
      UserErrorStatus[] errorStatuses = {
        UserErrorStatus.NOT_FOUND_USER,
        UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE,
        UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO,
        UserErrorStatus.DUPLICATED_EMAIL,
        UserErrorStatus.BAD_REQUEST_LOGIN,
        UserErrorStatus.ALREADY_SIGN_UP_USER
      };

      for (UserErrorStatus errorStatus : errorStatuses) {
        UserException userException = new UserException(errorStatus);

        assertThat(userException).isNotNull();
        assertAll(
            () -> assertThat(userException.getErrorCode()).isEqualTo(errorStatus),
            () -> assertThat(userException.getHttpStatus()).isEqualTo(errorStatus.getHttpStatus()),
            () -> assertThat(userException.getCode()).isEqualTo(errorStatus.getCode()),
            () -> assertThat(userException.getMessage()).isEqualTo(errorStatus.getMessage()));
      }
    }
  }

  @Nested
  @DisplayName("예외 상속 관계 테스트")
  class ExceptionInheritanceTest {

    @Test
    @DisplayName("성공: UserException은 BusinessException을 상속받는다")
    void success_userExceptionExtendsBusinessException() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.NOT_FOUND_USER;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      assertThat(userException)
          .isInstanceOf(com.dataracy.modules.common.exception.BusinessException.class)
          .isInstanceOf(com.dataracy.modules.common.exception.CustomException.class)
          .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("성공: UserException은 BusinessException의 모든 기능을 상속받는다")
    void success_userExceptionInheritsBusinessExceptionFunctionality() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.NOT_FOUND_USER;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      // BusinessException의 getter 메서드들이 정상 작동하는지 확인
      assertAll(
          () -> assertThat(userException.getErrorCode()).isEqualTo(errorStatus),
          () -> assertThat(userException.getHttpStatus()).isEqualTo(errorStatus.getHttpStatus()),
          () -> assertThat(userException.getCode()).isEqualTo(errorStatus.getCode()),
          () -> assertThat(userException.getMessage()).isEqualTo(errorStatus.getMessage()));
    }
  }

  @Nested
  @DisplayName("HTTP 상태 코드 테스트")
  class HttpStatusCodeTest {

    @Test
    @DisplayName("성공: NOT_FOUND_USER는 404 상태 코드를 가진다")
    void success_notFoundUserHas404StatusCode() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.NOT_FOUND_USER;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      assertThat(userException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("성공: FORBIDDEN_CHANGE_PASSWORD_*는 403 상태 코드를 가진다")
    void success_forbiddenChangePasswordHas403StatusCode() {
      // given
      UserErrorStatus googleError = UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE;
      UserErrorStatus kakaoError = UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO;

      // when
      UserException googleException = new UserException(googleError);
      UserException kakaoException = new UserException(kakaoError);

      // then
      assertAll(
          () -> assertThat(googleException.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN),
          () -> assertThat(kakaoException.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    @DisplayName("성공: DUPLICATED_EMAIL은 409 상태 코드를 가진다")
    void success_duplicatedEmailHas409StatusCode() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.DUPLICATED_EMAIL;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      assertThat(userException.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("성공: BAD_REQUEST_LOGIN는 400 상태 코드를 가진다")
    void success_badRequestLoginHas400StatusCode() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.BAD_REQUEST_LOGIN;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      assertThat(userException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("성공: ALREADY_SIGN_UP_USER는 409 상태 코드를 가진다")
    void success_alreadySignUpUserHas409StatusCode() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.ALREADY_SIGN_UP_USER;

      // when
      UserException userException = new UserException(errorStatus);

      // then
      assertThat(userException.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
  }

  @Nested
  @DisplayName("예외 메시지 테스트")
  class ExceptionMessageTest {

    @Test
    @DisplayName("성공: 각 에러 상태에 맞는 메시지를 반환한다")
    void success_returnAppropriateMessageForEachErrorStatus() {
      // given & when & then
      assertAll(
          () ->
              assertThat(new UserException(UserErrorStatus.NOT_FOUND_USER).getMessage())
                  .isEqualTo("해당 유저가 존재하지 않습니다."),
          () ->
              assertThat(
                      new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE)
                          .getMessage())
                  .isEqualTo("구글 로그인으로 가입된 계정은 비밀번호를 변경할 수 없습니다"),
          () ->
              assertThat(
                      new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO)
                          .getMessage())
                  .isEqualTo("카카오 로그인으로 가입된 계정은 비밀번호를 변경할 수 없습니다"),
          () ->
              assertThat(new UserException(UserErrorStatus.DUPLICATED_EMAIL).getMessage())
                  .isEqualTo("중복된 이메일은 사용할 수 없습니다"),
          () ->
              assertThat(new UserException(UserErrorStatus.BAD_REQUEST_LOGIN).getMessage())
                  .isEqualTo("이메일 또는 비밀번호를 확인해주세요"),
          () ->
              assertThat(new UserException(UserErrorStatus.ALREADY_SIGN_UP_USER).getMessage())
                  .isEqualTo("이미 가입된 계정입니다."));
    }

    @Test
    @DisplayName("성공: 예외 메시지가 null이 아니다")
    void success_exceptionMessageIsNotNull() {
      // given
      UserErrorStatus[] errorStatuses = UserErrorStatus.values();

      // when & then
      for (UserErrorStatus errorStatus : errorStatuses) {
        UserException userException = new UserException(errorStatus);
        assertAll(
            () -> assertThat(userException.getMessage()).isNotNull(),
            () -> assertThat(userException.getMessage()).isNotBlank());
      }
    }
  }

  @Nested
  @DisplayName("예외 코드 테스트")
  class ExceptionCodeTest {

    @Test
    @DisplayName("성공: 각 에러 상태에 맞는 코드를 반환한다")
    void success_returnAppropriateCodeForEachErrorStatus() {
      // given & when & then
      assertAll(
          () ->
              assertThat(new UserException(UserErrorStatus.NOT_FOUND_USER).getCode())
                  .isEqualTo("USER-002"),
          () ->
              assertThat(
                      new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE).getCode())
                  .isEqualTo("USER-019"),
          () ->
              assertThat(
                      new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO).getCode())
                  .isEqualTo("USER-018"),
          () ->
              assertThat(new UserException(UserErrorStatus.DUPLICATED_EMAIL).getCode())
                  .isEqualTo("USER-005"),
          () ->
              assertThat(new UserException(UserErrorStatus.BAD_REQUEST_LOGIN).getCode())
                  .isEqualTo("USER-020"),
          () ->
              assertThat(new UserException(UserErrorStatus.ALREADY_SIGN_UP_USER).getCode())
                  .isEqualTo("USER-001"));
    }

    @Test
    @DisplayName("성공: 예외 코드가 null이 아니다")
    void success_exceptionCodeIsNotNull() {
      // given
      UserErrorStatus[] errorStatuses = UserErrorStatus.values();

      // when & then
      for (UserErrorStatus errorStatus : errorStatuses) {
        UserException userException = new UserException(errorStatus);
        assertAll(
            () -> assertThat(userException.getCode()).isNotNull(),
            () -> assertThat(userException.getCode()).isNotBlank());
      }
    }
  }

  @Nested
  @DisplayName("예외 동등성 테스트")
  class ExceptionEqualityTest {

    @Test
    @DisplayName("성공: 같은 UserErrorStatus로 생성된 예외는 동일한 속성을 가진다")
    void success_exceptionsWithSameErrorStatusHaveSameProperties() {
      // given
      UserErrorStatus errorStatus = UserErrorStatus.NOT_FOUND_USER;

      // when
      UserException exception1 = new UserException(errorStatus);
      UserException exception2 = new UserException(errorStatus);

      // then
      assertAll(
          () -> assertThat(exception1.getErrorCode()).isEqualTo(exception2.getErrorCode()),
          () -> assertThat(exception1.getHttpStatus()).isEqualTo(exception2.getHttpStatus()),
          () -> assertThat(exception1.getCode()).isEqualTo(exception2.getCode()),
          () -> assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage()));
    }

    @Test
    @DisplayName("성공: 다른 UserErrorStatus로 생성된 예외는 다른 속성을 가진다")
    void success_exceptionsWithDifferentErrorStatusHaveDifferentProperties() {
      // given
      UserErrorStatus errorStatus1 = UserErrorStatus.NOT_FOUND_USER;
      UserErrorStatus errorStatus2 = UserErrorStatus.DUPLICATED_EMAIL;

      // when
      UserException exception1 = new UserException(errorStatus1);
      UserException exception2 = new UserException(errorStatus2);

      // then
      assertAll(
          () -> assertThat(exception1.getErrorCode()).isNotEqualTo(exception2.getErrorCode()),
          () -> assertThat(exception1.getHttpStatus()).isNotEqualTo(exception2.getHttpStatus()),
          () -> assertThat(exception1.getCode()).isNotEqualTo(exception2.getCode()),
          () -> assertThat(exception1.getMessage()).isNotEqualTo(exception2.getMessage()));
    }
  }
}
