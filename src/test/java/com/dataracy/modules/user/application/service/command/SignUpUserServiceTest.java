package com.dataracy.modules.user.application.service.command;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.application.mapper.command.CreateUserDtoMapper;
import com.dataracy.modules.user.application.port.in.validate.DuplicateEmailUseCase;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.application.service.command.signup.SignUpUserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpUserServiceTest {

    @Mock PasswordEncoder passwordEncoder;
    @Mock CreateUserDtoMapper createUserDtoMapper;
    @Mock UserCommandPort userCommandPort;
    @Mock DuplicateEmailUseCase duplicateEmailUseCase;
    @Mock DuplicateNicknameUseCase duplicateNicknameUseCase;
    @Mock JwtGenerateUseCase jwtGenerateUseCase;
    @Mock JwtValidateUseCase jwtValidateUseCase;
    @Mock ValidateAuthorLevelUseCase validateAuthorLevelUseCase;
    @Mock ValidateOccupationUseCase validateOccupationUseCase;
    @Mock ValidateVisitSourceUseCase validateVisitSourceUseCase;
    @Mock ValidateTopicUseCase validateTopicUseCase;
    @Mock ManageRefreshTokenUseCase manageRefreshTokenUseCase;

    @InjectMocks
    SignUpUserService service;

    private User savedUser() {
        return User.builder()
                .id(1L)
                .email("u@test.com")
                .nickname("nick")
                .password("encoded")
                .build();
    }

    @Test
    @DisplayName("signUpSelf: 정상 플로우 → 저장 및 토큰 발급")
    void signUpSelf_success() {
        // given
        SelfSignUpRequest req = new SelfSignUpRequest(
                "u@test.com",           // email
                "pw",                   // password
                "pw",                   // passwordConfirm
                "nick",                 // nickname
                1L,                     // authorLevelId
                2L,                     // occupationId
                Collections.emptyList(),// topicIds
                3L,                     // visitSourceId
                true                    // isAdTermsAgreed
        );

        given(passwordEncoder.encode("pw")).willReturn("encoded");
        given(createUserDtoMapper.toDomain(any(), anyString(), eq("encoded"))).willReturn(savedUser());
        given(userCommandPort.saveUser(any(User.class))).willReturn(savedUser());
        given(jwtGenerateUseCase.generateRefreshToken(1L, RoleType.ROLE_USER)).willReturn("refresh");
        given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(1000L);

        // when
        RefreshTokenResponse res = service.signUpSelf(req);

        // then
        assertThat(res.refreshToken()).isEqualTo("refresh");
        then(manageRefreshTokenUseCase).should().saveRefreshToken("1","refresh");
    }

    @Test
    @DisplayName("signUpSelf: 이메일 중복 → DUPLICATED_EMAIL 예외")
    void signUpSelf_duplicatedEmail() {
        // given
        SelfSignUpRequest req = new SelfSignUpRequest(
                "dup@test.com", "pw", "pw", "nick",
                1L, null, Collections.emptyList(), null, true
        );
        willThrow(new UserException(UserErrorStatus.DUPLICATED_EMAIL))
                .given(duplicateEmailUseCase).validateDuplicatedEmail("dup@test.com");

        // when
        UserException ex = catchThrowableOfType(() -> service.signUpSelf(req), UserException.class);

        // then
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.DUPLICATED_EMAIL);
    }
}
