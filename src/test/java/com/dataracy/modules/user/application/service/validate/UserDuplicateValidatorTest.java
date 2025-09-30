package com.dataracy.modules.user.application.service.validate;

import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.application.port.out.validate.ValidateUserExistsPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDuplicateValidatorTest {

    @Mock ValidateUserExistsPort userExistencePort;
    @Mock UserQueryPort userQueryPort;

    @InjectMocks UserDuplicateValidator validator;

    private User localUser() {
        return User.builder()
                .id(1L)
                .provider(ProviderType.LOCAL)
                .providerId("local-1")
                .role(RoleType.ROLE_USER)
                .email("u@test.com")
                .password("encoded")
                .nickname("nick")
                .topicIds(Collections.emptyList())
                .isDeleted(false)
                .build();
    }

    // ---------------- duplicateNickname ----------------
    @Test
    @DisplayName("duplicateNickname: 닉네임이 중복되지 않으면 예외 없음")
    void duplicateNicknameSuccess() {
        // given
        given(userExistencePort.existsByNickname("nick")).willReturn(false);

        // when & then
        assertThatCode(() -> validator.duplicateNickname("nick"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("duplicateNickname: 닉네임이 중복되면 DUPLICATED_NICKNAME 예외 발생")
    void duplicateNicknameDuplicated() {
        // given
        given(userExistencePort.existsByNickname("dupNick")).willReturn(true);

        // when
        UserException ex = catchThrowableOfType(
                () -> validator.duplicateNickname("dupNick"),
                UserException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_NICKNAME);
    }

    // ---------------- duplicateEmail ----------------
    @Test
    @DisplayName("duplicateEmail: 해당 이메일이 존재하면 User 반환")
    void duplicateEmailExists() {
        // given
        given(userQueryPort.findUserByEmail("u@test.com"))
                .willReturn(Optional.of(localUser()));

        // when
        Optional<User> result = validator.duplicateEmail("u@test.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("u@test.com");
    }

    @Test
    @DisplayName("duplicateEmail: 해당 이메일이 존재하지 않으면 Optional.empty 반환")
    void duplicateEmailNotExists() {
        // given
        given(userQueryPort.findUserByEmail("none@test.com"))
                .willReturn(Optional.empty());

        // when
        Optional<User> result = validator.duplicateEmail("none@test.com");

        // then
        assertThat(result).isEmpty();
    }
}
