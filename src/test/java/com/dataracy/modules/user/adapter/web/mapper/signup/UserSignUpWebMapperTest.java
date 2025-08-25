package com.dataracy.modules.user.adapter.web.mapper.signup;

import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserSignUpWebMapperTest {

    UserSignUpWebMapper mapper = new UserSignUpWebMapper();

    @Test
    @DisplayName("toApplicationDto(SelfSignUp): 웹 → 앱 매핑")
    void toApplicationDto_self() {
        // given
        SelfSignUpWebRequest web = new SelfSignUpWebRequest(
                "u@test.com", "pw", "pw", "nick",
                1L, 2L, List.of(10L, 20L), 3L, true
        );

        // when
        SelfSignUpRequest dto = mapper.toApplicationDto(web);

        // then
        assertThat(dto.email()).isEqualTo("u@test.com");
        assertThat(dto.password()).isEqualTo("pw");
        assertThat(dto.passwordConfirm()).isEqualTo("pw");
        assertThat(dto.nickname()).isEqualTo("nick");
        assertThat(dto.authorLevelId()).isEqualTo(1L);
        assertThat(dto.occupationId()).isEqualTo(2L);
        assertThat(dto.topicIds()).containsExactly(10L, 20L);
        assertThat(dto.visitSourceId()).isEqualTo(3L);
        assertThat(dto.isAdTermsAgreed()).isTrue();
    }

    @Test
    @DisplayName("toApplicationDto(Onboarding): 웹 → 앱 매핑")
    void toApplicationDto_onboarding() {
        // given
        OnboardingWebRequest web = new OnboardingWebRequest(
                "nick", 1L, 2L, List.of(10L, 20L), 3L, true
        );

        // when
        OnboardingRequest dto = mapper.toApplicationDto(web);

        // then
        assertThat(dto.nickname()).isEqualTo("nick");
        assertThat(dto.authorLevelId()).isEqualTo(1L);
        assertThat(dto.occupationId()).isEqualTo(2L);
        assertThat(dto.topicIds()).containsExactly(10L, 20L);
        assertThat(dto.visitSourceId()).isEqualTo(3L);
        assertThat(dto.isAdTermsAgreed()).isTrue();
    }
}
