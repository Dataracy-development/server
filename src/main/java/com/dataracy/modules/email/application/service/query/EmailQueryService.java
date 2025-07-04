package com.dataracy.modules.email.application.service.query;

import com.dataracy.modules.email.application.port.in.EmailVerifyUseCase;
import com.dataracy.modules.email.application.port.out.EmailRedisPort;
import com.dataracy.modules.email.domain.exception.EmailException;
import com.dataracy.modules.email.domain.status.EmailErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailQueryService implements EmailVerifyUseCase {

    private final EmailRedisPort emailRedisPort;

    /**
     * 이메일 인증코드 검증
     * @param email 이메일
     * @param code 인증 코드
     */
    public void verifyCode(String email, String code) {
        String savedCode = emailRedisPort.verifyCode(email, code);
        if (!savedCode.equals(code)) {
            throw new EmailException(EmailErrorStatus.FAIL_VERIFY_EMAIL_CODE);
        }
    }
}
