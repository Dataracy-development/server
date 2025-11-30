package com.dataracy.modules.user.application.service.command.password;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageResetTokenUseCase;
import com.dataracy.modules.auth.application.port.out.RateLimitPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;
import com.dataracy.modules.user.application.port.in.command.password.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {
  private final PasswordEncoder passwordEncoder;

  private final UserQueryPort userQueryPort;
  private final UserCommandPort userCommandPort;

  private final ManageResetTokenUseCase manageResetTokenUseCase;
  private final JwtValidateUseCase jwtValidateUseCase;

  @Qualifier("redisRateLimitAdapter")
  private final RateLimitPort rateLimitPort;

  private static final String USE_CASE = "ChangePasswordUseCase";

  /**
   * 주어진 유저의 비밀번호를 새 비밀번호로 변경한다. Google 또는 Kakao 계정으로 가입한 유저는 비밀번호를 변경할 수 없으며, 존재하지 않는 유저 ID가 입력된 경우
   * 예외가 발생한다.
   *
   * @param userId 비밀번호를 변경할 유저의 ID
   * @param requestDto 새 비밀번호와 비밀번호 확인값이 포함된 요청 DTO
   * @throws UserException 존재하지 않는 유저이거나 비밀번호 변경이 허용되지 않는 경우 발생
   */
  @Override
  @Transactional
  public void changePassword(Long userId, ChangePasswordRequest requestDto) {
    Instant startTime =
        LoggerFactory.service().logStart(USE_CASE, "비밀번호 변경 서비스 시작 userId=" + userId);
    // 비밀번호 - 비밀번호 확인 검증
    requestDto.validatePasswordMatch();

    // 해당 유저가 비밀번호를 변경할 수 있는 상태인지 확인한다.
    User savedUser =
        userQueryPort
            .findUserById(userId)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(USE_CASE, "[비밀번호 변경] 사용자를 찾을 수 없습니다. userId=" + userId);
                  return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
    savedUser.validatePasswordChangable();

    // 패스워드 암호화 및 변경
    String encodedPassword = passwordEncoder.encode(requestDto.password());
    userCommandPort.changePassword(userId, encodedPassword);

    LoggerFactory.service().logSuccess(USE_CASE, "비밀번호 변경 서비스 성공 userId=" + userId, startTime);
  }

  /**
   * 비밀번호 재설정 토큰을 사용해 사용자의 비밀번호를 재설정합니다.
   *
   * <p>요청에 포함된 리셋 토큰의 유효성을 확인하고 토큰에서 추출한 이메일로 사용자를 조회합니다. 새 비밀번호와 확인 값이 일치하는지 검증한 뒤 비밀번호를 인코딩하여
   * 저장합니다.
   *
   * @param requestDto 리셋 토큰, 새 비밀번호 및 비밀번호 확인 값을 포함한 요청 객체
   * @throws UserException 리셋 토큰이 유효하지 않거나(또는 만료되었거나), 사용자를 찾을 수 없거나, 또는 해당 사용자가 비밀번호 변경이 불가능한 경우 발생
   */
  @Override
  @Transactional
  public void resetPassword(ResetPasswordWithTokenRequest requestDto) {
    resetPassword(requestDto, null);
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordWithTokenRequest requestDto, String clientIp) {
    Instant startTime = LoggerFactory.service().logStart(USE_CASE, "비밀번호 재설정 서비스 시작");

    // 토큰 유효성 검사
    manageResetTokenUseCase.isValidResetToken(requestDto.resetPasswordToken());
    boolean valid = manageResetTokenUseCase.isValidResetToken(requestDto.resetPasswordToken());
    if (!valid) {
      LoggerFactory.service().logWarning(USE_CASE, "[비밀번호 재설정] 유효하지 않은 리셋 토큰입니다.");
      throw new UserException(UserErrorStatus.INVALID_OR_EXPIRED_RESET_PASSWORD_TOKEN);
    }

    String email = jwtValidateUseCase.getEmailFromResetToken(requestDto.resetPasswordToken());

    // Rate Limiting 검증 (IP가 제공된 경우)
    if (clientIp != null && !clientIp.trim().isEmpty()) {
      validateResetPasswordRateLimit(email, clientIp);
    }

    // 비밀번호 - 비밀번호 확인 검증
    requestDto.validatePasswordMatch();

    User savedUser =
        userQueryPort
            .findUserByEmail(email)
            .orElseThrow(
                () -> {
                  LoggerFactory.service().logWarning(USE_CASE, "[비밀번호 재설정] 사용자를 찾을 수 없습니다.");
                  return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
    savedUser.validatePasswordChangable();

    // 패스워드 암호화 및 변경
    String encodedPassword = passwordEncoder.encode(requestDto.password());
    userCommandPort.changePassword(savedUser.getId(), encodedPassword);

    LoggerFactory.service().logSuccess(USE_CASE, "비밀번호 재설정 서비스 성공", startTime);
  }

  /**
   * 비밀번호 재설정 레이트 리미팅 검증
   *
   * <p>다층 방어 전략: 1. IP별 제한: 같은 IP에서 무한 비밀번호 재설정 시도 방지 2. 이메일별 제한: 같은 이메일로 무한 비밀번호 재설정 시도 방지
   */
  private void validateResetPasswordRateLimit(String email, String clientIp) {
    // 1. IP별 제한: 같은 IP에서 여러 비밀번호 재설정 시도 방지
    String ipKey = "reset-password:ip:" + clientIp;
    int ipMaxRequests = 5; // IP당 5회/시간

    if (!rateLimitPort.isAllowed(ipKey, ipMaxRequests, 60)) {
      LoggerFactory.service()
          .logWarning(
              USE_CASE,
              String.format(
                  "비밀번호 재설정 IP별 레이트 리미팅 초과 - IP: %s, 제한: %d회/시간", clientIp, ipMaxRequests));
      throw new UserException(UserErrorStatus.RATE_LIMIT_EXCEEDED);
    }

    // 2. 이메일별 제한: 같은 이메일로 무한 비밀번호 재설정 시도 방지
    String emailKey = "reset-password:email:" + email.toLowerCase();
    int emailMaxRequests = 3; // 이메일당 3회/시간

    if (!rateLimitPort.isAllowed(emailKey, emailMaxRequests, 60)) {
      LoggerFactory.service()
          .logWarning(
              USE_CASE,
              String.format(
                  "비밀번호 재설정 이메일별 레이트 리미팅 초과 - 이메일: %s, 제한: %d회/시간", email, emailMaxRequests));
      throw new UserException(UserErrorStatus.RATE_LIMIT_EXCEEDED);
    }

    LoggerFactory.service()
        .logInfo(USE_CASE, String.format("비밀번호 재설정 레이트 리미팅 통과 - 이메일: %s, IP: %s", email, clientIp));
  }
}
