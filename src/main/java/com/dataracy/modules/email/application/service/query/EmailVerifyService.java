package com.dataracy.modules.email.application.service.query;

import com.dataracy.modules.auth.application.port.in.jwt.JwtGenerateUseCase;
import com.dataracy.modules.auth.application.port.in.cache.CacheResetTokenUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.application.port.in.validate.VerifyEmailUseCase;
import com.dataracy.modules.email.application.port.out.cache.CacheEmailPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerifyService implements VerifyEmailUseCase {
    private final CacheEmailPort cacheEmailPort;
    private final CacheResetTokenUseCase cacheResetTokenUseCase;

    private final JwtGenerateUseCase jwtGenerateUseCase;
    /**
     * 이메일 인증코드를 검증하고, 비밀번호 찾기 용도일 경우 리셋 토큰을 반환합니다.
     *
     * 이메일과 인증코드, 인증 목적을 받아 저장된 인증코드와 일치 여부를 확인합니다.
     * 인증코드가 만료되었거나 일치하지 않으면 `EmailException`을 발생시킵니다.
     * 인증이 성공하면 인증코드를 삭제하며, 인증 목적이 비밀번호 찾기일 경우 리셋 비밀번호 토큰을 생성하여 반환합니다.
     *
     * @param email 인증을 진행할 이메일 주소
     * @param code 사용자가 입력한 인증 코드
     * @param verificationType 이메일 인증 목적 (예: 회원가입, 비밀번호 찾기 등)
     * @return 비밀번호 찾기 인증일 경우 리셋 비밀번호 토큰, 그 외에는 null
     * @throws EmailException 인증코드가 만료되었거나 일치하지 않을 때 발생
     */
    @Override
    public String verifyCode(String email, String code, EmailVerificationType verificationType) {
        Instant startTime = LoggerFactory.service().logStart("VerifyEmailUseCase", "이메일 인증코드 검증 서비스 시작 email=" + email);

        // 레디스에서 이메일 인증 코드 조회
        String savedCode = cacheEmailPort.verifyCode(email, code, verificationType);
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
        cacheEmailPort.deleteCode(email, verificationType);

        String resetPasswordToken = null;
        if (verificationType.equals(EmailVerificationType.PASSWORD_SEARCH)) {
            resetPasswordToken = jwtGenerateUseCase.generateResetPasswordToken(email);
            cacheResetTokenUseCase.saveResetToken(resetPasswordToken);
        }

        LoggerFactory.service().logSuccess("VerifyEmailUseCase", "이메일 인증코드 검증 서비스 종료 email=" + email, startTime);
        return resetPasswordToken;
    }
}
