package com.dataracy.modules.auth.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;

/** AuthWebMapper 테스트 */
@DisplayName("AuthWebMapper 테스트")
class AuthWebMapperTest {

  private final AuthWebMapper authWebMapper = new AuthWebMapper();

  @Test
  @DisplayName("자체 로그인 웹 요청 DTO를 애플리케이션 요청 DTO로 변환")
  void toApplicationDtoSelfLoginWebRequestConversionSuccess() {
    // given
    String email = "test@example.com";
    String password = "password1";
    SelfLoginWebRequest webRequest = new SelfLoginWebRequest(email, password);

    // when
    SelfLoginRequest result = authWebMapper.toApplicationDto(webRequest);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.email()).isEqualTo(email),
        () -> assertThat(result.password()).isEqualTo(password));
  }
}
