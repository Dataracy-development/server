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
        if (savedCode == null) {
            throw new EmailException(EmailErrorStatus.EXPIRED_EMAIL_CODE);
        }
        if (!savedCode.equals(code)) {
            throw new EmailException(EmailErrorStatus.FAIL_VERIFY_EMAIL_CODE);
        }
        // 검증 완료 후 레디스에서 삭제
        // 트래잭션 정합성을 유지해야 하는 경우는 afterCommit을 사용하지만 검증 후 삭제는 생략해도 비즈니스 로직상 문제가 없다.
        emailRedisPort.deleteCode(email);
    }
}
