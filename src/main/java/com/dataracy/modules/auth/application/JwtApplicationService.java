package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.infra.jwt.JwtProperties;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import com.dataracy.modules.user.domain.enums.RoleStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtApplicationService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * Access Token 또는 Refresh Token 생성.
     *
     * @param userId          사용자 ID
     * @param expirationMillis 토큰 유효기간 (밀리초)
     * @return 생성된 토큰 문자열
     */
    public String generateAccessOrRefreshToken(Long userId, RoleStatusType role, long expirationMillis) {
        try {
            return jwtUtil.generateToken(Map.of("userId", userId, "role", role.getValue()), expirationMillis);
        } catch (Exception e){
            log.error("Failed to generate jwt token");
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_JWT_TOKEN);
        }
    }

    /**
     * Register Token 생성.
     *
     * @param provider   OAuth2 제공자
     * @param providerId 제공자 ID
     * @param email      사용자 이메일
     * @return 생성된 Register Token
     */
    public String generateRegisterToken(String provider, String providerId, String email) {
        try {
            return jwtUtil.generateToken(
                    Map.of("provider", provider, "providerId", providerId, "email", email),
                    jwtProperties.getRegisterTokenExpirationTime()
            );
        } catch (Exception e){
            log.error("Failed to generate jwt token");
            throw new AuthException(AuthErrorStatus.FAILED_GENERATE_REGISTER_TOKEN);
        }
    }
}
