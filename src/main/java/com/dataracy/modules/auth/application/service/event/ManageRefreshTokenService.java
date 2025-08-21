package com.dataracy.modules.auth.application.service.event;

import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ManageRefreshTokenService implements ManageRefreshTokenUseCase {
    private final ManageRefreshTokenPort manageRefreshTokenPort;

    /**
     * 지정한 사용자 ID에 대한 리프레시 토큰을 Redis에 저장합니다.
     *
     * @param userId 리프레시 토큰을 저장할 대상 사용자 ID
     * @param refreshToken 저장할 리프레시 토큰 값
     */
    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        Instant startTime = LoggerFactory.service().logStart("ManageRefreshTokenUseCase", "리프레시 토큰 레디스 저장 서비스 시작 userId=" + userId);
        manageRefreshTokenPort.saveRefreshToken(userId, refreshToken);
        LoggerFactory.service().logSuccess("ManageRefreshTokenUseCase", "리프레시 토큰 레디스 저장 서비스 성공 userId=" + userId, startTime);
    }

    /**
     * 주어진 유저 ID에 해당하는 리프레시 토큰을 레디스에서 조회하여 반환한다.
     *
     * @param userId 리프레시 토큰을 조회할 유저의 ID
     * @return 조회된 리프레시 토큰 문자열
     * @throws AuthException 리프레시 토큰이 존재하지 않거나 만료된 경우 발생
     */
    @Override
    public String getRefreshToken(String userId) {
        Instant startTime = LoggerFactory.service().logStart("ManageRefreshTokenUseCase", "레디스에서 리프레시 토큰 추출 서비스 시작 userId=" + userId);
        String refreshToken = manageRefreshTokenPort.getRefreshToken(userId);
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }
        LoggerFactory.service().logSuccess("ManageRefreshTokenUseCase", "레디스에서 리프레시 토큰 추출 서비스 성공 userId=" + userId, startTime);
        return refreshToken;
    }

    /**
     * 주어진 유저 ID에 해당하는 리프레시 토큰을 레디스에서 삭제한다.
     *
     * @param userId 리프레시 토큰을 삭제할 대상 유저의 ID
     */
    @Override
    public void deleteRefreshToken(String userId) {
        Instant startTime = LoggerFactory.service().logStart("ManageRefreshTokenUseCase", "레디스에서 리프레시 토큰 삭제 서비스 시작 userId=" + userId);
        manageRefreshTokenPort.deleteRefreshToken(userId);
        LoggerFactory.service().logSuccess("ManageRefreshTokenUseCase", "레디스에서 리프레시 토큰 삭제 서비스 성공 userId=" + userId, startTime);
    }
}
