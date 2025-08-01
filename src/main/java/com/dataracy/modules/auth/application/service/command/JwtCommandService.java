package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtCommandService implements JwtGenerateUseCase {
    private final JwtGeneratorPort jwtGeneratorPort;

    /**
     * Register Token 발급
     *
     * @param provider OAuth2 제공자
     * @param providerId 제공자 ID
     * @param email 이메일
     * @return 생성된 Register Token 문자열
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

    /**
     * Access Token 발급
     *
     * @param userId 유저 ID
     * @param role 유저 Role
     * @return 생성된 어세스 토큰 문자열
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
     * Refresh Token 발급
     *
     * @param userId 유저 ID
     * @param role 유저 Role
     * @return 생성된 리프레시 토큰 문자열
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
