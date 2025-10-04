package com.dataracy.modules.email.adapter.web.mapper.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.email.adapter.web.request.command.SendEmailWebRequest;
import com.dataracy.modules.email.application.dto.request.command.SendEmailRequest;

class EmailCommandWebMapperTest {

  @Test
  @DisplayName("웹 DTO -> 앱 DTO 매핑")
  void toApplicationDto() {
    // given
    SendEmailWebRequest web = new SendEmailWebRequest("user@example.com", "SIGN_UP");
    EmailCommandWebMapper mapper = new EmailCommandWebMapper();

    // when
    SendEmailRequest dto = mapper.toApplicationDto(web);

    // then
    assertAll(
        () -> assertThat(dto.email()).isEqualTo("user@example.com"),
        () -> assertThat(dto.purpose()).isEqualTo("SIGN_UP"));
  }
}
