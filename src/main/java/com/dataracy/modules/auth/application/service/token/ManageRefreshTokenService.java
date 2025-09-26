package com.dataracy.modules.auth.application.service.token;

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
    private static final String USE_CASE = "ManageRefreshTokenUseCase";

    /**
     * 지정한 사용자 ID의 리프레시 토큰을 Redis에 저장합니다.
     *
     * 실제 저장은 ManageRefreshTokenPort에 위임됩니다.
     *
     * @param userId       리프레시 토큰을 저장할 대상 사용자 ID
     * @param refreshToken 저장할 리프레시 토큰 값
     */
    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        Instant startTime = LoggerFactory.service().logStart(USE_CASE, "리프레시 토큰 레디스 저장 서비스 시작 userId=" + userId);
        manageRefreshTokenPort.saveRefreshToken(userId, refreshToken);
        LoggerFactory.service().logSuccess(USE_CASE, "리프레시 토큰 레디스 저장 서비스 성공 userId=" + userId, startTime);
    }

    /**
     * 주어진 사용자 ID로 Redis에서 리프레시 토큰을 조회하여 반환한다.
     *
     * 조회된 토큰이 없으면 인증 예외를 발생시킨다.
     *
     * @param userId 조회 대상 사용자 ID
     * @return 조회된 리프레시 토큰 문자열
     * @throws AuthException 토큰이 존재하지 않거나 만료된 경우 (AuthErrorStatus.EXPIRED_REFRESH_TOKEN)
     */
    @Override
    public String getRefreshToken(String userId) {
        Instant startTime = LoggerFactory.service().logStart(USE_CASE, "레디스에서 리프레시 토큰 추출 서비스 시작 userId=" + userId);
        String refreshToken = manageRefreshTokenPort.getRefreshToken(userId);
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }
        LoggerFactory.service().logSuccess(USE_CASE, "레디스에서 리프레시 토큰 추출 서비스 성공 userId=" + userId, startTime);
        return refreshToken;
    }

    /**
     * 지정한 사용자 ID의 리프레시 토큰을 Redis에서 삭제한다.
     *
     * 실제 삭제는 ManageRefreshTokenPort에 위임한다.
     *
     * @param userId 삭제할 대상의 사용자 ID
     */
    @Override
    public void deleteRefreshToken(String userId) {
        Instant startTime = LoggerFactory.service().logStart(USE_CASE, "레디스에서 리프레시 토큰 삭제 서비스 시작 userId=" + userId);
        manageRefreshTokenPort.deleteRefreshToken(userId);
        LoggerFactory.service().logSuccess(USE_CASE, "레디스에서 리프레시 토큰 삭제 서비스 성공 userId=" + userId, startTime);
    }
}
