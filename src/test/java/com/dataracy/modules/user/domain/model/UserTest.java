package com.dataracy.modules.user.domain.model;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserTest {

    @Test
    @DisplayName("isPasswordMatch - 비밀번호가 일치하는 경우 true를 반환한다")
    void isPasswordMatch_WhenPasswordMatches_ReturnsTrue() {
        // given
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        
        User user = User.builder()
                .password(encodedPassword)
                .build();
        
        given(encoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // when
        boolean result = user.isPasswordMatch(encoder, rawPassword);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isPasswordMatch - 비밀번호가 일치하지 않는 경우 false를 반환한다")
    void isPasswordMatch_WhenPasswordDoesNotMatch_ReturnsFalse() {
        // given
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        
        User user = User.builder()
                .password(encodedPassword)
                .build();
        
        given(encoder.matches(rawPassword, encodedPassword)).willReturn(false);

        // when
        boolean result = user.isPasswordMatch(encoder, rawPassword);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validatePasswordChangable - GOOGLE 제공자일 때 UserException이 발생한다")
    void validatePasswordChangable_WhenGoogleProvider_ThrowsUserException() {
        // given
        User user = User.builder()
                .provider(ProviderType.GOOGLE)
                .build();

        // when
        UserException exception = catchThrowableOfType(user::validatePasswordChangable, UserException.class);
        
        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE);
    }

    @Test
    @DisplayName("validatePasswordChangable - KAKAO 제공자일 때 UserException이 발생한다")
    void validatePasswordChangable_WhenKakaoProvider_ThrowsUserException() {
        // given
        User user = User.builder()
                .provider(ProviderType.KAKAO)
                .build();

        // when
        UserException exception = catchThrowableOfType(user::validatePasswordChangable, UserException.class);
        
        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO);
    }

    @Test
    @DisplayName("validatePasswordChangable - LOCAL 제공자일 때 예외가 발생하지 않는다")
    void validatePasswordChangable_WhenLocalProvider_DoesNotThrowException() {
        // given
        User user = User.builder()
                .provider(ProviderType.LOCAL)
                .build();

        // when & then
        assertThatCode(user::validatePasswordChangable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toUserInfo - UserInfo로 변환한다")
    void toUserInfo_ConvertsToUserInfo() {
        // given
        User user = User.builder()
                .id(1L)
                .role(RoleType.ROLE_USER)
                .email("test@example.com")
                .nickname("TestUser")
                .authorLevelId(1L)
                .occupationId(2L)
                .topicIds(List.of(1L, 2L))
                .visitSourceId(3L)
                .profileImageUrl("http://example.com/profile.jpg")
                .introductionText("Hello, I am a test user.")
                .build();

        // when
        var result = user.toUserInfo();

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.role()).isEqualTo(RoleType.ROLE_USER);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.nickname()).isEqualTo("TestUser");
        assertThat(result.authorLevelId()).isEqualTo(1L);
        assertThat(result.occupationId()).isEqualTo(2L);
        assertThat(result.topicIds()).isEqualTo(List.of(1L, 2L));
        assertThat(result.visitSourceId()).isEqualTo(3L);
        assertThat(result.profileImageUrl()).isEqualTo("http://example.com/profile.jpg");
        assertThat(result.introductionText()).isEqualTo("Hello, I am a test user.");
    }

    @Test
    @DisplayName("builder - 모든 필드를 포함한 User 객체를 생성한다")
    void builder_WithAllFields_CreatesUser() {
        // when
        User user = User.builder()
                .id(1L)
                .provider(ProviderType.LOCAL)
                .providerId("local123")
                .role(RoleType.ROLE_USER)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("TestUser")
                .profileImageUrl("http://example.com/profile.jpg")
                .introductionText("Hello, I am a test user.")
                .isAdTermsAgreed(true)
                .authorLevelId(1L)
                .occupationId(2L)
                .topicIds(List.of(1L, 2L))
                .visitSourceId(3L)
                .isDeleted(false)
                .build();

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getProvider()).isEqualTo(ProviderType.LOCAL);
        assertThat(user.getProviderId()).isEqualTo("local123");
        assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getNickname()).isEqualTo("TestUser");
        assertThat(user.getProfileImageUrl()).isEqualTo("http://example.com/profile.jpg");
        assertThat(user.getIntroductionText()).isEqualTo("Hello, I am a test user.");
        assertThat(user.isAdTermsAgreed()).isTrue();
        assertThat(user.getAuthorLevelId()).isEqualTo(1L);
        assertThat(user.getOccupationId()).isEqualTo(2L);
        assertThat(user.getTopicIds()).isEqualTo(List.of(1L, 2L));
        assertThat(user.getVisitSourceId()).isEqualTo(3L);
        assertThat(user.isDeleted()).isFalse();
    }
}