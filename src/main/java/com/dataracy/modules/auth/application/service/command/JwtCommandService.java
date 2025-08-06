package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtCommandService implements JwtGenerateUseCase {
    private final JwtGeneratorPort jwtGeneratorPort;

    /****
     * OAuth2 제공자 정보와 이메일을 기반으로 회원가입(Register)용 JWT 토큰을 생성합니다.
     *
     * @param provider OAuth2 제공자 이름
     * @param providerId 제공자에서 발급한 사용자 ID
     * @param email 사용자 이메일 주소
     * @return 생성된 회원가입(Register)용 JWT 토큰 문자열
     * @throws AuthException 토큰 생성에 실패한 경우 발생
     */
    @Override
    public String generateRegisterToken(String provider, String providerId, String email) {
        try {
            Instant startTime = LoggerFactory.service().logStart("JwtGenerateUseCase", "레지스터 토큰 발급 서비스 시작 email=" + email);
            String registerToken = jwtGeneratorPort.generateRegisterToken(provider, providerId, email);
            LoggerFactory.service().logSuccess("JwtGenerateUseCase", "레지스터 토큰 발급 서비스 성공 email=" + email, startTime);
            return registerToken;
        } catch (Exception e){
            LoggerFactory.service().logException("JwtGenerateUseCase", "[토큰 발급] 레지스터 토큰 발급에 실패했습니다. email=" + email, e);
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_REGISTER_TOKEN);
        }
    }

    @Override
    public String generateResetPasswordToken(String email) {
        try {
            Instant startTime = LoggerFactory.service().logStart("JwtGenerateUseCase", "패스워드 재설정 토큰 발급 서비스 시작 email=" + email);
            String resetPasswordToken = jwtGeneratorPort.generateResetPasswordToken(email);
            LoggerFactory.service().logSuccess("JwtGenerateUseCase", "패스워드 재설정 토큰 발급 서비스 성공 email=" + email, startTime);
            return resetPasswordToken;
        } catch (Exception e){
            LoggerFactory.service().logException("JwtGenerateUseCase", "[토큰 발급] 패스워드 재설정 토큰 발급에 실패했습니다. email=" + email, e);
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_RESET_PASSWORD_TOKEN);
        }
    }

    /**
     * 주어진 사용자 ID와 역할을 기반으로 어세스 토큰을 생성합니다.
     *
     * @param userId 토큰을 발급할 사용자 ID
     * @param role 사용자의 역할 정보
     * @return 생성된 어세스 토큰 문자열
     * @throws AuthException 어세스 토큰 생성에 실패한 경우 발생
     */
    @Override
    public String generateAccessToken(Long userId, RoleType role) {
        try {
            Instant startTime = LoggerFactory.service().logStart("JwtGenerateUseCase", "어세스 토큰 발급 서비스 시작 userId=" + userId);
            String accessToken = jwtGeneratorPort.generateAccessToken(userId, role);
            LoggerFactory.service().logSuccess("JwtGenerateUseCase", "어세스 토큰 발급 서비스 성공 userId=" + userId, startTime);
            return accessToken;
        } catch (Exception e){
            LoggerFactory.service().logException("JwtGenerateUseCase", "[토큰 발급] 어세스 토큰 발급에 실패했습니다. userId=" + userId, e);
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN);
        }
    }

    /**
     * 지정된 사용자 ID와 역할을 기반으로 리프레시 토큰을 생성합니다.
     *
     * @param userId 토큰을 발급할 사용자 ID
     * @param role 토큰을 발급할 사용자의 역할
     * @return 생성된 리프레시 토큰 문자열
     * @throws AuthException 토큰 생성에 실패한 경우 발생
     */
    @Override
    public String generateRefreshToken(Long userId, RoleType role) {
        try {
            Instant startTime = LoggerFactory.service().logStart("JwtGenerateUseCase", "리프레시 토큰 발급 서비스 시작 userId=" + userId);
            String refreshToken =  jwtGeneratorPort.generateRefreshToken(userId, role);
            LoggerFactory.service().logSuccess("JwtGenerateUseCase", "리프레시 토큰 발급 서비스 성공 userId=" + userId, startTime);
            return refreshToken;
        } catch (Exception e){
            LoggerFactory.service().logException("JwtGenerateUseCase", "[토큰 발급] 리프레시 토큰 발급에 실패했습니다. userId=" + userId, e);
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN);
        }
    }
}
