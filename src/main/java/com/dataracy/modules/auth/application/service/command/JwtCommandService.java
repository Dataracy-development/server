package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.adapter.jwt.JwtGeneratorAdapter;
import com.dataracy.modules.auth.application.port.in.JwtGenerateUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtCommandService implements JwtGenerateUseCase {
    private final JwtGeneratorAdapter jwtGeneratorAdapter;

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
            return jwtGeneratorAdapter.generateRegisterToken(provider, providerId, email);
        } catch (Exception e){
            log.error("Failed to generate register token from provider : {}, email : {}", provider, email);
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
            return jwtGeneratorAdapter.generateAccessToken(userId, role);
        } catch (Exception e){
            log.error("Failed to generate access token for userId : {}", userId);
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
            return jwtGeneratorAdapter.generateRefreshToken(userId, role);
        } catch (Exception e){
            log.error("Failed to generate refresh token for userId : {}", userId);
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_REFRESH_TOKEN);
        }
    }
}
