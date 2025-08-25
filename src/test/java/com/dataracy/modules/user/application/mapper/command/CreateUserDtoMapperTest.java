package com.dataracy.modules.user.application.mapper.command;

import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserDtoMapperTest {

    private CreateUserDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CreateUserDtoMapper();
        ReflectionTestUtils.setField(mapper, "defaultProfileImageUrl", "default.png");
    }

    @Test
    @DisplayName("SelfSignUpRequest → User 변환 시 필드 매핑 검증")
    void toDomain_selfSignUpRequest() {
        // given
        SelfSignUpRequest req = new SelfSignUpRequest(
                "test@email.com",
                "pw",
                "pw",
                "tester",
                1L,
                2L,
                List.of(10L, 20L),
                3L,
                true
        );

        String providerId = "uuid-123";
        String encodedPassword = "encodedPw";

        // when
        User user = mapper.toDomain(req, providerId, encodedPassword);

        // then
        assertThat(user.getProvider()).isEqualTo(ProviderType.LOCAL);
        assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER);
        assertThat(user.getEmail()).isEqualTo("test@email.com");
        assertThat(user.getPassword()).isEqualTo("encodedPw");
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getAuthorLevelId()).isEqualTo(1L);
        assertThat(user.getOccupationId()).isEqualTo(2L);
        assertThat(user.getTopicIds()).containsExactly(10L, 20L);
        assertThat(user.getVisitSourceId()).isEqualTo(3L);
        assertThat(user.getProfileImageUrl()).isEqualTo("default.png");
        assertThat(user.getIntroductionText()).isEqualTo("안녕하세요. tester입니다.");
        assertThat(user.isAdTermsAgreed()).isTrue();
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("OnboardingRequest → User 변환 시 필드 매핑 검증")
    void toDomain_onboardingRequest() {
        // given
        OnboardingRequest req = new OnboardingRequest(
                "tester",
                1L,
                2L,
                List.of(10L, 20L),
                3L,
                true
        );

        String provider = "GOOGLE";
        String providerId = "google-123";
        String email = "google@test.com";

        // when
        User user = mapper.toDomain(req, provider, providerId, email);

        // then
        assertThat(user.getProvider()).isEqualTo(ProviderType.GOOGLE);
        assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER);
        assertThat(user.getProviderId()).isEqualTo("google-123");
        assertThat(user.getEmail()).isEqualTo("google@test.com");
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getAuthorLevelId()).isEqualTo(1L);
        assertThat(user.getOccupationId()).isEqualTo(2L);
        assertThat(user.getTopicIds()).containsExactly(10L, 20L);
        assertThat(user.getVisitSourceId()).isEqualTo(3L);
        assertThat(user.getProfileImageUrl()).isEqualTo("default.png");
        assertThat(user.getIntroductionText()).isEqualTo("안녕하세요. tester입니다.");
        assertThat(user.isAdTermsAgreed()).isTrue();
        assertThat(user.isDeleted()).isFalse();
    }
}
