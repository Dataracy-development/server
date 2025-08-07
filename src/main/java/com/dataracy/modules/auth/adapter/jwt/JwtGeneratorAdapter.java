package com.dataracy.modules.auth.adapter.jwt;

import com.dataracy.modules.auth.application.port.out.jwt.JwtGeneratorPort;
import com.dataracy.modules.auth.domain.enums.TokenType;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * jwt 토큰 발급 로직
 */
@Component
@RequiredArgsConstructor
public class JwtGeneratorAdapter implements JwtGeneratorPort {
    private final JwtUtilInternal jwtUtilInternal;
    private final JwtProperties jwtProperties;

    @Override
    public String generateResetPasswordToken(
            String email
    ) {
        return jwtUtilInternal.generateToken(
                Map.of("email", email),
                jwtProperties.getResetTokenExpirationTime(),
                TokenType.RESET_PASSWORD
        );
    }

    /**
     * 온보딩 추가 정보 입력에서 사용하는 레지스터 토큰 발급
     *
     * @param provider 소셜 제공자
     * @param providerId 소셜에서 받은 유저 고유 아이디
     * @param email 이메일
     * @return 레지스터 토큰
     */
    @Override
    public String generateRegisterToken(
            String provider,
            String providerId,
            String email
    ) {
        return jwtUtilInternal.generateToken(
                Map.of("provider", provider, "providerId", providerId, "email", email),
                jwtProperties.getRegisterTokenExpirationTime(),
                TokenType.REGISTER
        );
    }

    /**
     * 어세스 토큰 발급
     *
     * @param userId 유저 아이디
     * @param role 유저 역할
     * @return 어세스 토큰
     */
    @Override
    public String generateAccessToken(Long userId, RoleType role) {
        return jwtUtilInternal.generateToken(
                Map.of("userId", userId, "role", role.getValue()),
                jwtProperties.getAccessTokenExpirationTime(),
                TokenType.ACCESS
        );
    }

    /**
     * 리프레시 토큰 발급
     *
     * @param userId 유저 아이디
     * @param role 유저 역할
     * @return 리프레시 토큰
     */
    @Override
    public String generateRefreshToken(Long userId, RoleType role) {
        return jwtUtilInternal.generateToken(
                Map.of("userId", userId, "role", role.getValue()),
                jwtProperties.getRefreshTokenExpirationTime(),
                TokenType.REFRESH
        );
    }
}
