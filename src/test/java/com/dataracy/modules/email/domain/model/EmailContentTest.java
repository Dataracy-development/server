package com.dataracy.modules.email.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EmailContent 테스트")
class EmailContentTest {

  @Test
  @DisplayName("EmailContent record 생성 및 속성 확인")
  void emailContentShouldCreateCorrectly() {
    // Given
    String subject = "테스트 이메일";
    String content = "이것은 테스트 이메일 내용입니다.";

    // When
    EmailContent emailContent = new EmailContent(subject, content);

    // Then
    assertAll(
        () -> assertThat(emailContent.subject()).isEqualTo(subject),
        () -> assertThat(emailContent.body()).isEqualTo(content));
  }

  @Test
  @DisplayName("EmailContent record equals 및 hashCode 테스트")
  void emailContentShouldHaveCorrectEqualsAndHashCode() {
    // Given
    EmailContent emailContent1 = new EmailContent("제목", "내용");
    EmailContent emailContent2 = new EmailContent("제목", "내용");
    EmailContent emailContent3 = new EmailContent("다른 제목", "내용");

    // When & Then
    assertThat(emailContent1)
        .isEqualTo(emailContent2)
        .hasSameHashCodeAs(emailContent2)
        .isNotEqualTo(emailContent3);
  }

  @Test
  @DisplayName("EmailContent record toString 테스트")
  void emailContentShouldHaveCorrectToString() {
    // Given
    EmailContent emailContent = new EmailContent("인증 이메일", "인증 코드: 1456");

    // When
    String toString = emailContent.toString();

    // Then
    assertThat(toString).contains("EmailContent").contains("인증 이메일").contains("인증 코드: 1456");
  }

  @Test
  @DisplayName("EmailContent record - null 값 처리")
  void emailContentShouldHandleNullValues() {
    // Given & When
    EmailContent emailContent = new EmailContent(null, null);

    // Then
    assertAll(
        () -> assertThat(emailContent.subject()).isNull(),
        () -> assertThat(emailContent.body()).isNull());
  }

  @Test
  @DisplayName("EmailContent record - 다양한 이메일 타입들 테스트")
  void emailContentShouldHandleVariousEmailTypes() {
    // Given & When
    EmailContent welcomeEmail = new EmailContent("환영합니다", "회원가입을 환영합니다.");
    EmailContent passwordResetEmail = new EmailContent("비밀번호 재설정", "비밀번호를 재설정해주세요.");
    EmailContent notificationEmail = new EmailContent("알림", "새로운 알림이 있습니다.");

    // Then
    assertAll(
        () -> assertThat(welcomeEmail.subject()).isEqualTo("환영합니다"),
        () -> assertThat(passwordResetEmail.subject()).isEqualTo("비밀번호 재설정"),
        () -> assertThat(notificationEmail.subject()).isEqualTo("알림"),
        () -> assertThat(welcomeEmail.body()).isEqualTo("회원가입을 환영합니다."),
        () -> assertThat(passwordResetEmail.body()).isEqualTo("비밀번호를 재설정해주세요."),
        () -> assertThat(notificationEmail.body()).isEqualTo("새로운 알림이 있습니다."));
  }

  @Test
  @DisplayName("EmailContent record - 빈 문자열 처리")
  void emailContentShouldHandleEmptyStrings() {
    // Given & When
    EmailContent emailContent = new EmailContent("", "");

    // Then
    assertAll(
        () -> assertThat(emailContent.subject()).isEmpty(),
        () -> assertThat(emailContent.body()).isEmpty());
  }
}
