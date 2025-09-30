package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * JWT 토큰 생성 로직을 공통화하여 중복 제거
 */
@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final JwtGeneratorPort jwtGeneratorPort;

    // Use Case 상수 정의
    private static final String JWT_GENERATE_USE_CASE = "JwtGenerateUseCase";

    /**
     * 공통 토큰 생성 로직
     */
    private String generateTokenWithLogging(String tokenType, String identifier, TokenGenerationFunction generator) {
        try {
            Instant startTime = LoggerFactory.service().logStart(JWT_GENERATE_USE_CASE, 
                String.format("%s 토큰 발급 서비스 시작 %s", tokenType, identifier));
            
            String token = generator.generate();
            
            LoggerFactory.service().logSuccess(JWT_GENERATE_USE_CASE, 
                String.format("%s 토큰 발급 서비스 성공 %s", tokenType, identifier), startTime);
            
            return token;
        } catch (Exception e) {
            LoggerFactory.service().logException(JWT_GENERATE_USE_CASE, 
                String.format("[토큰 발급] %s 토큰 발급에 실패했습니다. %s", tokenType, identifier), e);
            throw new AuthException(getErrorStatus(tokenType));
        }
    }

    /**
     * 레지스터 토큰 생성
     */
    public String generateRegisterToken(String provider, String providerId, String email) {
        return generateTokenWithLogging("레지스터", "email=" + email, 
            () -> jwtGeneratorPort.generateRegisterToken(provider, providerId, email));
    }

    /**
     * 패스워드 재설정 토큰 생성
     */
    public String generateResetPasswordToken(String email) {
        return generateTokenWithLogging("패스워드 재설정", "email=" + email, 
            () -> jwtGeneratorPort.generateResetPasswordToken(email));
    }

    /**
     * 액세스 토큰 생성
     */
    public String generateAccessToken(Long userId, RoleType role) {
        return generateTokenWithLogging("액세스", "userId=" + userId, 
            () -> jwtGeneratorPort.generateAccessToken(userId, role));
    }

    /**
     * 리프레시 토큰 생성
     */
    public String generateRefreshToken(Long userId, RoleType role) {
        return generateTokenWithLogging("리프레시", "userId=" + userId, 
            () -> jwtGeneratorPort.generateRefreshToken(userId, role));
    }

    /**
     * 토큰 타입별 에러 상태 반환
     */
    private AuthErrorStatus getErrorStatus(String tokenType) {
        return switch (tokenType) {
            case "레지스터" -> AuthErrorStatus.FAILED_GENERATE_REGISTER_TOKEN;
            case "패스워드 재설정" -> AuthErrorStatus.FAILED_GENERATE_RESET_PASSWORD_TOKEN;
            case "액세스" -> AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN;
            case "리프레시" -> AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN;
            default -> AuthErrorStatus.FAILED_GENERATE_ACCESS_TOKEN;
        };
    }

    /**
     * 토큰 생성 함수 인터페이스
     * RuntimeException을 사용하여 JWT 생성 실패를 표현한다.
     */
    @FunctionalInterface
    private interface TokenGenerationFunction {
        String generate();
    }
}
