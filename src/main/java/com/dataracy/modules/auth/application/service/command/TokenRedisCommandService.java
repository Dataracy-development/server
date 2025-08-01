package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.auth.application.port.out.redis.TokenRedisPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenRedisCommandService implements TokenRedisUseCase {
    private final TokenRedisPort tokenRedisPort;

    /**
     * 리프레시 토큰을 저장합니다.
     *
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰
     */
    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        Instant startTime = LoggerFactory.service().logStart("TokenRedisUseCase", "리프레시 토큰 레디스 저장 서비스 시작 userId=" + userId);
        tokenRedisPort.saveRefreshToken(userId, refreshToken);
        LoggerFactory.service().logSuccess("TokenRedisUseCase", "리프레시 토큰 레디스 저장 서비스 성공 userId=" + userId, startTime);
    }

    /**
     * 유저 id에 해당하는 리프레시 토큰을 레디스에서 추출한다.
     * @param userId 유저 id
     * @return 레디스의 리프레시 토큰 문자열
     */
    @Override
    public String getRefreshToken(String userId) {
        Instant startTime = LoggerFactory.service().logStart("TokenRedisUseCase", "레디스에서 리프레시 토큰 추출 서비스 시작 userId=" + userId);
        String refreshToken = tokenRedisPort.getRefreshToken(userId);
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }
        LoggerFactory.service().logSuccess("TokenRedisUseCase", "레디스에서 리프레시 토큰 추출 서비스 성공 userId=" + userId, startTime);
        return refreshToken;
    }

    /**
     * 유저 id에 해당하는 리프레시 토큰을 레디스에서 삭제한다.
     * @param userId 유저 id
     */
    @Override
    public void deleteRefreshToken(String userId) {
        Instant startTime = LoggerFactory.service().logStart("TokenRedisUseCase", "레디스에서 리프레시 토큰 삭제 서비스 시작 userId=" + userId);
        tokenRedisPort.deleteRefreshToken(userId);
        LoggerFactory.service().logSuccess("TokenRedisUseCase", "레디스에서 리프레시 토큰 삭제 서비스 성공 userId=" + userId, startTime);
    }
}
