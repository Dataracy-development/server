package com.dataracy.modules.email.application.service.query;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.redis.ResetTokenRedisUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.application.port.in.validate.VerifyEmailUseCase;
import com.dataracy.modules.email.application.port.out.EmailCachePort;
import com.dataracy.modules.auth.application.port.out.redis.ResetPasswordCachePort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerifyService implements VerifyEmailUseCase {
    private final EmailCachePort emailCachePort;
    private final ResetTokenRedisUseCase resetTokenRedisUseCase;

    private final JwtGenerateUseCase jwtGenerateUseCase;
    /**
     * 이메일 인증코드 검증
     * @param email 이메일
     * @param code 인증 코드
     * @param verificationType 이메일 인증 목적
     */
    @Override
    public String verifyCode(String email, String code, EmailVerificationType verificationType) {
        Instant startTime = LoggerFactory.service().logStart("VerifyEmailUseCase", "이메일 인증코드 검증 서비스 시작 email=" + email);

        // 레디스에서 이메일 인증 코드 조회
        String savedCode = emailCachePort.verifyCode(email, code, verificationType);
        if (savedCode == null) {
            LoggerFactory.service().logWarning("VerifyEmailUseCase", "이메일 인증코드가 만료되었습니다. email=" + email);
            throw new EmailException(EmailErrorStatus.EXPIRED_EMAIL_CODE);
        }

        // 이메일 인증코드 일치하지 않을 경우
        if (!savedCode.equals(code)) {
            LoggerFactory.service().logWarning("VerifyEmailUseCase", "이메일 인증코드가 일치하지 않습니다. email=" + email);
            throw new EmailException(EmailErrorStatus.FAIL_VERIFY_EMAIL_CODE);
        }

        // 검증 완료 후 레디스에서 삭제
        // 트래잭션 정합성을 유지해야 하는 경우는 afterCommit을 사용하지만 검증 후 삭제는 생략해도 비즈니스 로직상 문제가 없다.
        emailCachePort.deleteCode(email, verificationType);

        String resetPasswordToken = null;
        if (verificationType.equals(EmailVerificationType.PASSWORD_SEARCH)) {
            resetPasswordToken = jwtGenerateUseCase.generateResetPasswordToken(email);
            resetTokenRedisUseCase.saveResetToken(resetPasswordToken);
        }

        LoggerFactory.service().logSuccess("VerifyEmailUseCase", "이메일 인증코드 검증 서비스 종료 email=" + email, startTime);
        return resetPasswordToken;
    }
}
