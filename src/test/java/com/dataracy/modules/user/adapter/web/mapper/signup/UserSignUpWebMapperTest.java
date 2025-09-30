package com.dataracy.modules.user.adapter.web.mapper.signup;

import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSignUpWebMapper 테스트")
class UserSignUpWebMapperTest {

    private final UserSignUpWebMapper mapper = new UserSignUpWebMapper();

    @Test
    @DisplayName("SelfSignUpWebRequest를 SelfSignUpRequest로 변환 성공")
    void toApplicationDto_ShouldConvertSelfSignUpRequest() {
        // Given
        SelfSignUpWebRequest webRequest = new SelfSignUpWebRequest(
                "test@example.com",
                "password123",
                "password123",
                "testuser",
                1L,
                2L,
                List.of(1L, 2L),
                3L,
                true
        );

        // When
        SelfSignUpRequest applicationRequest = mapper.toApplicationDto(webRequest);

        // Then
        assertThat(applicationRequest).isNotNull();
        assertThat(applicationRequest.email()).isEqualTo("test@example.com");
        assertThat(applicationRequest.password()).isEqualTo("password123");
        assertThat(applicationRequest.passwordConfirm()).isEqualTo("password123");
        assertThat(applicationRequest.nickname()).isEqualTo("testuser");
        assertThat(applicationRequest.authorLevelId()).isEqualTo(1L);
        assertThat(applicationRequest.occupationId()).isEqualTo(2L);
        assertThat(applicationRequest.topicIds()).containsExactly(1L, 2L);
        assertThat(applicationRequest.visitSourceId()).isEqualTo(3L);
        assertThat(applicationRequest.isAdTermsAgreed()).isTrue();
    }

    @Test
    @DisplayName("OnboardingWebRequest를 OnboardingRequest로 변환 성공")
    void toApplicationDto_ShouldConvertOnboardingRequest() {
        // Given
        OnboardingWebRequest webRequest = new OnboardingWebRequest(
                "nickname",
                1L,
                2L,
                List.of(1L, 2L, 3L),
                3L,
                false
        );

        // When
        OnboardingRequest applicationRequest = mapper.toApplicationDto(webRequest);

        // Then
        assertThat(applicationRequest).isNotNull();
        assertThat(applicationRequest.nickname()).isEqualTo("nickname");
        assertThat(applicationRequest.authorLevelId()).isEqualTo(1L);
        assertThat(applicationRequest.occupationId()).isEqualTo(2L);
        assertThat(applicationRequest.topicIds()).containsExactly(1L, 2L, 3L);
        assertThat(applicationRequest.visitSourceId()).isEqualTo(3L);
        assertThat(applicationRequest.isAdTermsAgreed()).isFalse();
    }

    @Test
    @DisplayName("빈 토픽 리스트로 변환 성공")
    void toApplicationDto_ShouldConvertWithEmptyTopics() {
        // Given
        SelfSignUpWebRequest webRequest = new SelfSignUpWebRequest(
                "test@example.com",
                "password123",
                "password123",
                "testuser",
                1L,
                null,
                List.of(),
                null,
                true
        );

        // When
        SelfSignUpRequest applicationRequest = mapper.toApplicationDto(webRequest);

        // Then
        assertThat(applicationRequest).isNotNull();
        assertThat(applicationRequest.topicIds()).isEmpty();
        assertThat(applicationRequest.occupationId()).isNull();
        assertThat(applicationRequest.visitSourceId()).isNull();
    }
}