package com.dataracy.modules.user.domain.model;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class UserTest {

    @Test
    @DisplayName("isPasswordMatch: 인코더가 일치(true) 반환 시 true")
    void isPasswordMatchShouldReturnTrueWhenEncoderMatches() {
        // given
        String encoded = "$2a$10$abcdefg"; // dummy encoded
        User user = User.builder()
                .id(1L)
                .provider(ProviderType.LOCAL)
                .providerId("local-1")
                .role(RoleType.ROLE_USER)
                .email("user@test.com")
                .password(encoded)
                .nickname("june")
                .authorLevelId(2L)
                .occupationId(3L)
                .topicIds(Arrays.asList(1L, 2L))
                .visitSourceId(4L)
                .profileImageUrl("img.png")
                .introductionText("hi")
                .isAdTermsAgreed(true)
                .isDeleted(false)
                .build();
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        given(passwordEncoder.matches("raw", encoded)).willReturn(true);

        // when
        boolean result = user.isPasswordMatch(passwordEncoder, "raw");

        // then
        assertThat(result).isTrue();
        then(passwordEncoder).should().matches("raw", encoded);
    }

    @Test
    @DisplayName("isPasswordMatch: 인코더가 불일치(false) 반환 시 false")
    void isPasswordMatchShouldReturnFalseWhenEncoderNotMatches() {
        // given
        String encoded = "$2a$10$abcdefg";
        User user = User.builder()
                .id(1L)
                .provider(ProviderType.LOCAL)
                .providerId("local-1")
                .role(RoleType.ROLE_USER)
                .email("user@test.com")
                .password(encoded)
                .nickname("june")
                .authorLevelId(2L)
                .occupationId(3L)
                .topicIds(Arrays.asList(1L))
                .visitSourceId(4L)
                .profileImageUrl("img.png")
                .introductionText("hi")
                .isAdTermsAgreed(false)
                .isDeleted(false)
                .build();
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        given(passwordEncoder.matches("wrong", encoded)).willReturn(false);

        // when
        boolean result = user.isPasswordMatch(passwordEncoder, "wrong");

        // then
        assertThat(result).isFalse();
        then(passwordEncoder).should().matches("wrong", encoded);
    }

    @Nested
    @DisplayName("validatePasswordChangable")
    class ValidatePasswordChangableTest {

        @Test
        @DisplayName("LOCAL 사용자는 예외 없이 통과")
        void validatePasswordChangable_shouldPass_whenLocal() {
            // given
            User user = User.builder()
                    .id(1L)
                    .provider(ProviderType.LOCAL)
                    .providerId("local-1")
                    .role(RoleType.ROLE_USER)
                    .email("user@test.com")
                    .password("encoded")
                    .nickname("june")
                    .authorLevelId(2L)
                    .occupationId(3L)
                    .topicIds(Arrays.asList(1L, 2L))
                    .visitSourceId(4L)
                    .profileImageUrl("img.png")
                    .introductionText("hi")
                    .isAdTermsAgreed(true)
                    .isDeleted(false)
                    .build();

            // when & then
            assertThatCode(user::validatePasswordChangable).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("KAKAO 사용자는 비밀번호 변경 금지 예외 발생")
        void validatePasswordChangableShouldThrowForKakao() {
            // given
            User user = User.builder()
                    .id(1L)
                    .provider(ProviderType.KAKAO)
                    .providerId("kakao-1")
                    .role(RoleType.ROLE_USER)
                    .email("user@test.com")
                    .password("encoded")
                    .nickname("june")
                    .authorLevelId(2L)
                    .occupationId(3L)
                    .topicIds(Arrays.asList(1L))
                    .visitSourceId(4L)
                    .profileImageUrl("img.png")
                    .introductionText("hi")
                    .isAdTermsAgreed(true)
                    .isDeleted(false)
                    .build();

            // when
            UserException ex = catchThrowableOfType(user::validatePasswordChangable, UserException.class);

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO);
        }

        @Test
        @DisplayName("GOOGLE 사용자는 비밀번호 변경 금지 예외 발생")
        void validatePasswordChangableShouldThrowForGoogle() {
            // given
            User user = User.builder()
                    .id(1L)
                    .provider(ProviderType.GOOGLE)
                    .providerId("google-1")
                    .role(RoleType.ROLE_USER)
                    .email("user@test.com")
                    .password("encoded")
                    .nickname("june")
                    .authorLevelId(2L)
                    .occupationId(3L)
                    .topicIds(Arrays.asList(1L))
                    .visitSourceId(4L)
                    .profileImageUrl("img.png")
                    .introductionText("hi")
                    .isAdTermsAgreed(true)
                    .isDeleted(false)
                    .build();

            // when
            UserException ex = catchThrowableOfType(user::validatePasswordChangable, UserException.class);

            // then
            assertThat(ex).isNotNull();
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE);
        }
    }

    @Test
    @DisplayName("toUserInfo: 읽기용 VO 매핑 필드 검증")
    void toUserInfoShouldMapSelectedFields() {
        // given
        User user = User.builder()
                .id(10L)
                .provider(ProviderType.LOCAL)
                .providerId("local-10")
                .role(RoleType.ROLE_ADMIN)
                .email("admin@test.com")
                .password("encoded")
                .nickname("admin")
                .authorLevelId(7L)
                .occupationId(8L)
                .topicIds(Arrays.asList(100L, 200L))
                .visitSourceId(9L)
                .profileImageUrl("admin.png")
                .introductionText("intro")
                .isAdTermsAgreed(false)
                .isDeleted(false)
                .build();

        // when
        UserInfo info = user.toUserInfo();

        // then
        assertThat(info.id()).isEqualTo(10L);
        assertThat(info.role()).isEqualTo(RoleType.ROLE_ADMIN);
        assertThat(info.email()).isEqualTo("admin@test.com");
        assertThat(info.nickname()).isEqualTo("admin");
        assertThat(info.authorLevelId()).isEqualTo(7L);
        assertThat(info.occupationId()).isEqualTo(8L);
        assertThat(info.topicIds()).containsExactly(100L, 200L);
        assertThat(info.visitSourceId()).isEqualTo(9L);
        assertThat(info.profileImageUrl()).isEqualTo("admin.png");
        assertThat(info.introductionText()).isEqualTo("intro");
    }
}
