package com.dataracy.modules.user.application.service.query.auth;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;
import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.port.in.command.auth.HandleUserUseCase;
import com.dataracy.modules.user.application.port.in.query.auth.IsLoginPossibleUseCase;
import com.dataracy.modules.user.application.port.in.query.auth.IsNewUserUseCase;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthService
    implements IsNewUserUseCase, HandleUserUseCase, IsLoginPossibleUseCase {
  private final PasswordEncoder passwordEncoder;

  private final UserQueryPort userQueryPort;

  private final JwtValidateUseCase jwtValidateUseCase;
  private final JwtGenerateUseCase jwtGenerateUseCase;
  private final ManageRefreshTokenUseCase manageRefreshTokenUseCase;

  // Use Case 상수 정의
  private static final String IS_NEW_USER_USE_CASE = "IsNewUserUseCase";
  private static final String HANDLE_USER_USE_CASE = "HandleUserUseCase";
  private static final String IS_LOGIN_POSSIBLE_USE_CASE = "IsLoginPossibleUseCase";

  // 메시지 상수 정의
  private static final String USER_NOT_FOUND_MESSAGE = "이메일에 해당하는 유저가 존재하지 않습니다. email=";

  /**
   * 주어진 OAuth 사용자 정보로 해당 사용자가 기존에 존재하는지 확인하여 신규 사용자인지 여부를 반환합니다.
   *
   * @param oAuthUserInfo 소셜 인증에서 받은 사용자 정보
   * @return 사용자가 존재하지 않으면 true(신규 사용자), 존재하면 false
   */
  @Override
  @Transactional(readOnly = true)
  public boolean isNewUser(OAuthUserInfo oAuthUserInfo) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(IS_NEW_USER_USE_CASE, "신규 유저 여부 확인 서비스 시작 email=" + oAuthUserInfo.email());
    boolean isNew = userQueryPort.findUserByProviderId(oAuthUserInfo.providerId()).isEmpty();
    LoggerFactory.service()
        .logSuccess(
            IS_NEW_USER_USE_CASE, "신규 유저 여부 확인 서비스 성공 email=" + oAuthUserInfo.email(), startTime);

    return isNew;
  }

  /**
   * OAuth 제공자 정보를 기반으로 신규 사용자 등록용 JWT 레지스터 토큰과 만료 시간을 반환합니다.
   *
   * @param oAuthUserInfo OAuth 인증을 통해 획득한 사용자 정보
   * @return 등록 토큰과 만료 시간이 포함된 RegisterTokenResponse
   */
  @Override
  public RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(HANDLE_USER_USE_CASE, "신규 유저 시 핸들링 서비스 시작 email=" + oAuthUserInfo.email());
    String registerToken =
        jwtGenerateUseCase.generateRegisterToken(
            oAuthUserInfo.provider(), oAuthUserInfo.providerId(), oAuthUserInfo.email());
    RegisterTokenResponse registerTokenResponse =
        new RegisterTokenResponse(
            registerToken, jwtValidateUseCase.getRegisterTokenExpirationTime());
    LoggerFactory.service()
        .logSuccess(
            HANDLE_USER_USE_CASE, "신규 유저 핸들링 서비스 성공 email=" + oAuthUserInfo.email(), startTime);

    return registerTokenResponse;
  }

  /**
   * OAuth 제공자 ID로 기존 사용자를 조회해 리프레시 토큰을 생성·저장하고 토큰과 만료 시간을 반환합니다.
   *
   * <p>조회된 사용자가 없으면 UserException(UserErrorStatus.NOT_FOUND_USER)을 던집니다.
   *
   * @param oAuthUserInfo OAuth 제공자에서 받은 사용자 식별 정보(프로바이더, providerId 등)
   * @return 발급된 리프레시 토큰과 해당 토큰의 만료 시간을 담은 {@code RefreshTokenResponse}
   * @throws UserException 조회된 사용자가 없을 경우 {@code UserErrorStatus.NOT_FOUND_USER}
   */
  @Override
  @Transactional
  public RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(HANDLE_USER_USE_CASE, "기존 유저 핸들링 서비스 시작 email=" + oAuthUserInfo.email());
    User existUser =
        userQueryPort
            .findUserByProviderId(oAuthUserInfo.providerId())
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(
                          HANDLE_USER_USE_CASE, "[기존 유저 처리] 소셜 식별자 아이디에 해당하는 사용자를 찾을 수 없습니다");
                  return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

    String refreshToken =
        jwtGenerateUseCase.generateRefreshToken(existUser.getId(), existUser.getRole());
    manageRefreshTokenUseCase.saveRefreshToken(existUser.getId().toString(), refreshToken);
    RefreshTokenResponse refreshTokenResponse =
        new RefreshTokenResponse(refreshToken, jwtValidateUseCase.getRefreshTokenExpirationTime());

    LoggerFactory.service()
        .logSuccess(
            HANDLE_USER_USE_CASE, "기존 유저 핸들링 서비스 성공 email=" + oAuthUserInfo.email(), startTime);
    return refreshTokenResponse;
  }

  /**
   * 이메일과 비밀번호로 사용자를 인증하고, 인증에 성공하면 해당 사용자의 정보를 반환합니다. 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 경우
   * `UserException`이 발생합니다.
   *
   * @param email 인증에 사용할 이메일 주소
   * @param password 인증에 사용할 비밀번호
   * @return 인증에 성공한 사용자의 정보
   * @throws UserException 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 때 발생합니다.
   */
  @Override
  @Transactional
  public UserInfo checkLoginPossibleAndGetUserInfo(String email, String password) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                IS_LOGIN_POSSIBLE_USE_CASE,
                "입력받은 이메일, 비밀번호로 로그인이 가능한지 여부를 확인하는 서비스 시작 email=" + email);

    User user =
        userQueryPort
            .findUserByEmail(email)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(
                          IS_LOGIN_POSSIBLE_USE_CASE,
                          "[로그인 가능 여부] " + USER_NOT_FOUND_MESSAGE + email);
                  return new UserException(UserErrorStatus.BAD_REQUEST_LOGIN);
                });

    if (!user.isPasswordMatch(passwordEncoder, password)) {
      LoggerFactory.service()
          .logWarning(
              IS_LOGIN_POSSIBLE_USE_CASE,
              "[로그인 가능 여부] 제공받은 비밀번호와 실제 비밀번호가 일치하지 않습니다. 재로그인을 시도해주세요.");
      throw new UserException(UserErrorStatus.BAD_REQUEST_LOGIN);
    }

    UserInfo userInfo = user.toUserInfo();

    LoggerFactory.service()
        .logSuccess(
            IS_LOGIN_POSSIBLE_USE_CASE,
            "입력받은 이메일, 비밀번호로 로그인이 가능한지 여부를 확인하는 서비스 성공 email=" + email,
            startTime);
    return userInfo;
  }
}
