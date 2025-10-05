package com.dataracy.modules.user.application.service.command.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.application.mapper.command.CreateUserDtoMapper;
import com.dataracy.modules.user.application.port.in.validate.DuplicateEmailUseCase;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SignUpUserServiceTest {

  // Test constants
  private static final Long ONE_HOUR_IN_MILLIS = 3600000L;
  private static final Long ONE_WEEK_IN_MILLIS = 604800000L;
  private static final Long TWO_WEEKS_IN_MILLIS = 1209600000L;
  @InjectMocks private SignUpUserService service;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private CreateUserDtoMapper createUserDtoMapper;

  @Mock private UserCommandPort userCommandPort;

  @Mock private DuplicateNicknameUseCase duplicateNicknameUseCase;

  @Mock private DuplicateEmailUseCase duplicateEmailUseCase;

  @Mock private JwtGenerateUseCase jwtGenerateUseCase;

  @Mock private JwtValidateUseCase jwtValidateUseCase;

  @Mock private ValidateAuthorLevelUseCase validateAuthorLevelUseCase;

  @Mock private ValidateOccupationUseCase validateOccupationUseCase;

  @Mock private ValidateVisitSourceUseCase validateVisitSourceUseCase;

  @Mock private ValidateTopicUseCase validateTopicUseCase;

  @Mock private ManageRefreshTokenUseCase manageRefreshTokenUseCase;

  private SelfSignUpRequest createSampleSelfSignUpRequest() {
    return new SelfSignUpRequest(
        "test@example.com",
        "password1",
        "password1",
        "testuser",
        1L,
        1L,
        List.of(1L, 2L),
        1L,
        true);
  }

  private OnboardingRequest createSampleOnboardingRequest() {
    return new OnboardingRequest("testuser", 1L, 1L, List.of(1L, 2L), 1L, true);
  }

  private User createSampleUser() {
    return User.builder()
        .id(1L)
        .email("test@example.com")
        .nickname("testuser")
        .password("encodedPassword")
        .provider(ProviderType.LOCAL)
        .providerId("providerId")
        .introductionText("Test Introduction")
        .authorLevelId(1L)
        .occupationId(1L)
        .visitSourceId(1L)
        .topicIds(List.of(1L, 2L))
        .role(RoleType.ROLE_USER)
        .build();
  }

  @Nested
  @DisplayName("자체 회원가입")
  class SelfSignUp {

    @Test
    @DisplayName("자체 회원가입 성공")
    void signUpSelfSuccess() {
      // given
      SelfSignUpRequest request = createSampleSelfSignUpRequest();
      User user = createSampleUser();
      User savedUser = createSampleUser();
      String refreshToken = "generated-refresh-token";
      long expirationTime = ONE_HOUR_IN_MILLIS; // 1시간

      willDoNothing().given(duplicateEmailUseCase).validateDuplicatedEmail(anyString());
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
      given(createUserDtoMapper.toDomain(any(), anyString(), anyString())).willReturn(user);
      given(userCommandPort.saveUser(any())).willReturn(savedUser);
      given(jwtGenerateUseCase.generateRefreshToken(anyLong(), any(RoleType.class)))
          .willReturn(refreshToken);
      willDoNothing().given(manageRefreshTokenUseCase).saveRefreshToken(anyString(), anyString());
      given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(expirationTime);

      // when
      RefreshTokenResponse response = service.signUpSelf(request);

      // then
      assertThat(response.refreshToken()).isEqualTo(refreshToken);
      assertThat(response.refreshTokenExpiration()).isEqualTo(expirationTime);
      then(userCommandPort).should().saveUser(any());
      then(manageRefreshTokenUseCase).should().saveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("자체 회원가입 성공 - 선택적 필드 null")
    void signUpSelfSuccessWithNullOptionalFields() {
      // given
      SelfSignUpRequest request =
          new SelfSignUpRequest(
              "test@example.com",
              "password1",
              "password1",
              "testuser",
              1L,
              null, // occupationId null
              null, // topicIds null
              null, // visitSourceId null
              true);
      User user = createSampleUser();
      User savedUser = createSampleUser();
      String refreshToken = "generated-refresh-token";
      long expirationTime = ONE_HOUR_IN_MILLIS; // 1시간

      willDoNothing().given(duplicateEmailUseCase).validateDuplicatedEmail(anyString());
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
      given(createUserDtoMapper.toDomain(any(), anyString(), anyString())).willReturn(user);
      given(userCommandPort.saveUser(any())).willReturn(savedUser);
      given(jwtGenerateUseCase.generateRefreshToken(anyLong(), any(RoleType.class)))
          .willReturn(refreshToken);
      willDoNothing().given(manageRefreshTokenUseCase).saveRefreshToken(anyString(), anyString());
      given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(expirationTime);

      // when
      RefreshTokenResponse response = service.signUpSelf(request);

      // then
      assertThat(response.refreshToken()).isEqualTo(refreshToken);
      then(validateOccupationUseCase).should(never()).validateOccupation(anyLong());
      then(validateVisitSourceUseCase).should(never()).validateVisitSource(anyLong());
      then(validateTopicUseCase).should(never()).validateTopic(anyLong());
    }
  }

  @Nested
  @DisplayName("소셜 회원가입")
  class OAuthSignUp {

    @Test
    @DisplayName("소셜 회원가입 성공")
    void signUpOAuthSuccess() {
      // given
      String registerToken = "valid-register-token";
      OnboardingRequest request = createSampleOnboardingRequest();
      User user = createSampleUser();
      User savedUser = createSampleUser();
      String refreshToken = "generated-refresh-token";
      long expirationTime = ONE_HOUR_IN_MILLIS; // 1시간

      willDoNothing().given(jwtValidateUseCase).validateToken(anyString());
      given(jwtValidateUseCase.getProviderFromRegisterToken(registerToken)).willReturn("GOOGLE");
      given(jwtValidateUseCase.getProviderIdFromRegisterToken(registerToken)).willReturn("google1");
      given(jwtValidateUseCase.getEmailFromRegisterToken(registerToken))
          .willReturn("test@example.com");
      willDoNothing().given(duplicateEmailUseCase).validateDuplicatedEmail(anyString());
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      given(createUserDtoMapper.toDomain(any(), anyString(), anyString(), anyString()))
          .willReturn(user);
      given(userCommandPort.saveUser(any())).willReturn(savedUser);
      given(jwtGenerateUseCase.generateRefreshToken(anyLong(), any(RoleType.class)))
          .willReturn(refreshToken);
      willDoNothing().given(manageRefreshTokenUseCase).saveRefreshToken(anyString(), anyString());
      given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(expirationTime);

      // when
      RefreshTokenResponse response = service.signUpOAuth(registerToken, request);

      // then
      assertThat(response.refreshToken()).isEqualTo(refreshToken);
      assertThat(response.refreshTokenExpiration()).isEqualTo(expirationTime);
      then(userCommandPort).should().saveUser(any());
      then(manageRefreshTokenUseCase).should().saveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("소셜 회원가입 성공 - 빈 토픽 ID 리스트")
    void signUpOAuthSuccessWithEmptyTopicIds() {
      // given
      String registerToken = "valid-register-token";
      OnboardingRequest request =
          new OnboardingRequest(
              "testuser",
              1L,
              1L,
              List.of(), // 빈 토픽 ID 리스트
              1L,
              true);
      User user = createSampleUser();
      User savedUser = createSampleUser();
      String refreshToken = "generated-refresh-token";
      long expirationTime = ONE_HOUR_IN_MILLIS; // 1시간

      willDoNothing().given(jwtValidateUseCase).validateToken(anyString());
      given(jwtValidateUseCase.getProviderFromRegisterToken(registerToken)).willReturn("GOOGLE");
      given(jwtValidateUseCase.getProviderIdFromRegisterToken(registerToken)).willReturn("google1");
      given(jwtValidateUseCase.getEmailFromRegisterToken(registerToken))
          .willReturn("test@example.com");
      willDoNothing().given(duplicateEmailUseCase).validateDuplicatedEmail(anyString());
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      given(createUserDtoMapper.toDomain(any(), anyString(), anyString(), anyString()))
          .willReturn(user);
      given(userCommandPort.saveUser(any())).willReturn(savedUser);
      given(jwtGenerateUseCase.generateRefreshToken(anyLong(), any(RoleType.class)))
          .willReturn(refreshToken);
      willDoNothing().given(manageRefreshTokenUseCase).saveRefreshToken(anyString(), anyString());
      given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(expirationTime);

      // when
      RefreshTokenResponse response = service.signUpOAuth(registerToken, request);

      // then
      assertThat(response.refreshToken()).isEqualTo(refreshToken);
      then(validateTopicUseCase).should(never()).validateTopic(anyLong());
    }
  }

  @Nested
  @DisplayName("경계값 테스트")
  class BoundaryTests {

    @Test
    @DisplayName("자체 회원가입 성공 - 최소 필수 정보만")
    void signUpSelfSuccessMinimalRequiredInfo() {
      // given
      SelfSignUpRequest request =
          new SelfSignUpRequest(
              "minimal@example.com",
              "password1",
              "password1",
              "minimaluser",
              1L,
              null, // occupationId null
              null, // topicIds null
              null, // visitSourceId null
              false // isAdTermsAgreed false
              );
      User user = createSampleUser();
      User savedUser = createSampleUser();
      String refreshToken = "generated-refresh-token";
      long expirationTime = ONE_HOUR_IN_MILLIS; // 1시간

      willDoNothing().given(duplicateEmailUseCase).validateDuplicatedEmail(anyString());
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
      given(createUserDtoMapper.toDomain(any(), anyString(), anyString())).willReturn(user);
      given(userCommandPort.saveUser(any())).willReturn(savedUser);
      given(jwtGenerateUseCase.generateRefreshToken(anyLong(), any(RoleType.class)))
          .willReturn(refreshToken);
      willDoNothing().given(manageRefreshTokenUseCase).saveRefreshToken(anyString(), anyString());
      given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(expirationTime);

      // when
      RefreshTokenResponse response = service.signUpSelf(request);

      // then
      assertThat(response.refreshToken()).isEqualTo(refreshToken);
      then(userCommandPort).should().saveUser(any());
    }

    @Test
    @DisplayName("소셜 회원가입 성공 - 다른 소셜 제공자")
    void signUpOAuthSuccessDifferentProvider() {
      // given
      String registerToken = "valid-register-token";
      OnboardingRequest request = createSampleOnboardingRequest();
      User user = createSampleUser();
      User savedUser = createSampleUser();
      String refreshToken = "generated-refresh-token";
      long expirationTime = ONE_HOUR_IN_MILLIS; // 1시간

      willDoNothing().given(jwtValidateUseCase).validateToken(anyString());
      given(jwtValidateUseCase.getProviderFromRegisterToken(registerToken)).willReturn("KAKAO");
      given(jwtValidateUseCase.getProviderIdFromRegisterToken(registerToken)).willReturn("kakao1");
      given(jwtValidateUseCase.getEmailFromRegisterToken(registerToken))
          .willReturn("test@kakao.com");
      willDoNothing().given(duplicateEmailUseCase).validateDuplicatedEmail(anyString());
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      given(createUserDtoMapper.toDomain(any(), anyString(), anyString(), anyString()))
          .willReturn(user);
      given(userCommandPort.saveUser(any())).willReturn(savedUser);
      given(jwtGenerateUseCase.generateRefreshToken(anyLong(), any(RoleType.class)))
          .willReturn(refreshToken);
      willDoNothing().given(manageRefreshTokenUseCase).saveRefreshToken(anyString(), anyString());
      given(jwtValidateUseCase.getRefreshTokenExpirationTime()).willReturn(expirationTime);

      // when
      RefreshTokenResponse response = service.signUpOAuth(registerToken, request);

      // then
      assertThat(response.refreshToken()).isEqualTo(refreshToken);
      then(userCommandPort).should().saveUser(any());
    }
  }
}
