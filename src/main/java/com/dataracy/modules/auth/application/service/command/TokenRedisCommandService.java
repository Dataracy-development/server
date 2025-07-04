package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.in.TokenRedisUseCase;
import com.dataracy.modules.auth.application.port.out.TokenRedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
        tokenRedisPort.saveRefreshToken(userId, refreshToken);
    }

    /**
     * 유저 id에 해당하는 리프레시 토큰을 레디스에서 추출한다.
     * @param userId 유저 id
     * @return 레디스의 리프레시 토큰 문자열
     */
    @Override
    public String getRefreshToken(String userId) {
        return tokenRedisPort.getRefreshToken(userId);
    }

    /**
     * 유저 id에 해당하는 리프레시 토큰을 레디스에서 삭제한다.
     * @param userId 유저 id
     */
    @Override
    public void deleteRefreshToken(String userId) {
        tokenRedisPort.deleteRefreshToken(userId);
    }
}
