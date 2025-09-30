package com.dataracy.modules.auth.adapter.web.mapper;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AuthWebMapper 테스트
 */
@DisplayName("AuthWebMapper 테스트")
class AuthWebMapperTest {

    private final AuthWebMapper authWebMapper = new AuthWebMapper();

    @Test
    @DisplayName("자체 로그인 웹 요청 DTO를 애플리케이션 요청 DTO로 변환")
    void toApplicationDto_자체_로그인_웹_요청_변환_성공() {
        // given
        String email = "test@example.com";
        String password = "password123";
        SelfLoginWebRequest webRequest = new SelfLoginWebRequest(email, password);

        // when
        SelfLoginRequest result = authWebMapper.toApplicationDto(webRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.password()).isEqualTo(password);
    }

}
