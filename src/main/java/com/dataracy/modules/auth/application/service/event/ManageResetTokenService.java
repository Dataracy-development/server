package com.dataracy.modules.auth.application.service.event;

import com.dataracy.modules.auth.application.port.in.token.ManageResetTokenUseCase;
import com.dataracy.modules.auth.application.port.out.token.ManageResetTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ManageResetTokenService implements ManageResetTokenUseCase {
    private final ManageResetTokenPort manageResetTokenPort;

    /**
     * 비밀번호 재설정 토큰을 캐시에 저장합니다.
     *
     * @param token 저장할 비밀번호 재설정 토큰
     */
    @Override
    public void saveResetToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("CacheResetTokenUseCase", "비밀번호 재설정 토큰 레디스 저장 서비스 시작");
        manageResetTokenPort.saveResetToken(token);
        LoggerFactory.service().logSuccess("CacheResetTokenUseCase", "비밀번호 재설정 토큰 레디스 저장 서비스 성공", startTime);
    }

    /**
     * 주어진 비밀번호 재설정 토큰의 유효성을 검사합니다.
     *
     * 토큰이 유효하지 않은 경우 AuthException을 발생시키며, 유효한 경우 true를 반환합니다.
     *
     * @param token 검사할 비밀번호 재설정 토큰
     * @return 토큰이 유효하면 true
     * @throws AuthException 토큰이 만료되었거나 유효하지 않은 경우
     */
    @Override
    public boolean isValidResetToken(String token) {
        boolean isValid = manageResetTokenPort.isValidResetToken(token);
        if (!isValid) {
            LoggerFactory.service().logWarning("CacheResetTokenUseCase", "비밀번호 재설정 토큰이 만료되었습니다.");
            throw new AuthException(AuthErrorStatus.EXPIRED_RESET_PASSWORD_TOKEN);
        }
        return isValid;
    }
}
