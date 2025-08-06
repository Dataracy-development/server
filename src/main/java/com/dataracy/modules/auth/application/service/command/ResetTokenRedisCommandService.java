package com.dataracy.modules.auth.application.service.command;

import com.dataracy.modules.auth.application.port.in.redis.ResetTokenRedisUseCase;
import com.dataracy.modules.auth.application.port.in.redis.TokenRedisUseCase;
import com.dataracy.modules.auth.application.port.out.redis.ResetPasswordCachePort;
import com.dataracy.modules.auth.application.port.out.redis.TokenRedisPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ResetTokenRedisCommandService implements ResetTokenRedisUseCase {
    private final ResetPasswordCachePort resetPasswordCachePort;

    @Override
    public void saveResetToken(String token) {
        Instant startTime = LoggerFactory.service().logStart("ResetTokenRedisUseCase", "비밀번호 재설정 토큰 레디스 저장 서비스 시작");
        resetPasswordCachePort.saveResetToken(token);
        LoggerFactory.service().logSuccess("ResetTokenRedisUseCase", "비밀번호 재설정 토큰 레디스 저장 서비스 성공", startTime);
    }

    @Override
    public boolean isValidResetToken(String token) {
        boolean isValid = resetPasswordCachePort.isValidResetToken(token);
        if (!isValid) {
            LoggerFactory.service().logWarning("ValidateResetPasswordUseCase", "비밀번호 재설정 토큰이 만료되었습니다.");
            throw new AuthException(AuthErrorStatus.EXPIRED_RESET_PASSWORD_TOKEN);
        }
        return isValid;
    }
}
