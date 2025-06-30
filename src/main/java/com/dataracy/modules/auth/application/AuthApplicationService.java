package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import com.dataracy.modules.common.lock.DistributedLock;
import com.dataracy.modules.user.application.dto.response.ReIssueTokenResponseDto;
import com.dataracy.modules.user.domain.enums.RoleStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JwtApplicationService jwtApplicationService;
    private final JwtQueryService jwtQueryService;
    private final TokenApplicationService tokenApplicationService;

    /**
     * Refresh Token 검증 및 새로운 토큰 발급.
     *
     * @param refreshToken 클라이언트로부터 받은 리프레시 토큰
     * @return 새로 생성된 Access Token과 Refresh Token
     */
    @DistributedLock(key = "'lock:refresh-reissue:' + #refreshToken", waitTime = 200, leaseTime = 3000)
    public ReIssueTokenResponseDto reIssueToken(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }

        Long userId = tokenApplicationService.validateAndExtractUserId(refreshToken);
        String newAccessToken = jwtApplicationService.generateAccessOrRefreshToken(userId, RoleStatusType.ROLE_USER, jwtQueryService.getAccessTokenExpirationTime());
        String newRefreshToken = jwtApplicationService.generateAccessOrRefreshToken(userId, RoleStatusType.ROLE_USER, jwtQueryService.getRefreshTokenExpirationTime());
        return new ReIssueTokenResponseDto(userId, newAccessToken, newRefreshToken, jwtQueryService.getAccessTokenExpirationTime(), jwtQueryService.getRefreshTokenExpirationTime());
    }
}
