/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.domain.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.model.EmailContent;

@DisplayName("EmailContentFactory 테스트")
class EmailContentFactoryTest {

  @Nested
  @DisplayName("generate 메서드 테스트")
  class GenerateTest {

    @Test
    @DisplayName("성공: SIGN_UP 타입 이메일 내용 생성")
    void generate_SIGNUP타입_회원가입이메일내용생성() {
      // given
      EmailVerificationType type = EmailVerificationType.SIGN_UP;
      String code = "123456";

      // when
      EmailContent content = EmailContentFactory.generate(type, code);

      // then
      assertAll(
          () -> assertThat(content).isNotNull(),
          () -> assertThat(content.subject()).contains("회원가입 이메일 인증번호"),
          () -> assertThat(content.body()).contains(code),
          () -> assertThat(content.body()).contains("Dataracy 회원가입을 위한 인증번호"));
    }

    @Test
    @DisplayName("성공: PASSWORD_SEARCH 타입 이메일 내용 생성")
    void generate_PASSWORDSEARCH타입_비밀번호찾기이메일내용생성() {
      // given
      EmailVerificationType type = EmailVerificationType.PASSWORD_SEARCH;
      String code = "789012";

      // when
      EmailContent content = EmailContentFactory.generate(type, code);

      // then
      assertAll(
          () -> assertThat(content).isNotNull(),
          () -> assertThat(content.subject()).contains("비밀번호 찾기 인증번호"),
          () -> assertThat(content.body()).contains(code),
          () -> assertThat(content.body()).contains("비밀번호 찾기를 위한 인증번호"));
    }

    @Test
    @DisplayName("성공: PASSWORD_RESET 타입 이메일 내용 생성")
    void generate_PASSWORDRESET타입_비밀번호재설정이메일내용생성() {
      // given
      EmailVerificationType type = EmailVerificationType.PASSWORD_RESET;
      String code = "345678";

      // when
      EmailContent content = EmailContentFactory.generate(type, code);

      // then
      assertAll(
          () -> assertThat(content).isNotNull(),
          () -> assertThat(content.subject()).contains("비밀번호 재설정 인증번호"),
          () -> assertThat(content.body()).contains(code),
          () -> assertThat(content.body()).contains("비밀번호 재설정을 위한 인증번호"));
    }

    @Test
    @DisplayName("성공: 이메일 내용에 인증번호가 포함됨")
    void generate_인증번호포함_이메일내용에인증번호포함() {
      // given
      EmailVerificationType type = EmailVerificationType.SIGN_UP;
      String code = "999999";

      // when
      EmailContent content = EmailContentFactory.generate(type, code);

      // then
      assertThat(content.body()).contains("🔐 인증번호: 999999");
    }

    @Test
    @DisplayName("성공: 이메일 내용에 유효시간 정보 포함")
    void generate_유효시간정보_이메일내용에유효시간포함() {
      // given
      EmailVerificationType type = EmailVerificationType.SIGN_UP;
      String code = "111111";

      // when
      EmailContent content = EmailContentFactory.generate(type, code);

      // then
      assertThat(content.body()).contains("5분 동안 유효합니다");
    }

    @Test
    @DisplayName("성공: 빈 인증번호 처리")
    void generate_빈인증번호_정상처리() {
      // given
      EmailVerificationType type = EmailVerificationType.SIGN_UP;
      String code = "";

      // when
      EmailContent content = EmailContentFactory.generate(type, code);

      // then
      assertAll(
          () -> assertThat(content).isNotNull(),
          () -> assertThat(content.body()).contains("🔐 인증번호: "));
    }
  }
}
