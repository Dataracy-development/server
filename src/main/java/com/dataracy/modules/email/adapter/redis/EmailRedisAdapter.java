package com.dataracy.modules.email.adapter.redis;

import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.email.application.port.out.EmailRedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 레디스를 이용해 이메일 인증 저장 및 조회
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRedisAdapter implements EmailRedisPort {

    private final StringRedisTemplate redisTemplate;

    @Value("${aws.ses.expire-minutes}")
    private long EXPIRE_MINUTES;

    /**
     * 이메일 인증키 생성
     * @param email 이메일
     * @return 레디스 키
     */
    private String getEmailKey(String email) {
        return "email:verification: " + email;
    }

    /**
     * 이메일 인증코드 저장
     * @param email 이메일
     * @param code 인증 코드
     */
    public void saveCode(String email, String code) {
        try {
            redisTemplate.opsForValue().set(getEmailKey(email), code, EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("Saved email certification for email: {}", email);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            log.error("Data access exception while saving refresh token.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 레디스에 저장된 인증코드를 추출한다.
     * @param email 이메일
     * @param code 클라이언트로부터 받은 입력 코드
     */
    public String verifyCode(String email, String code) {
        String emailKey = getEmailKey(email);
        return redisTemplate.opsForValue().get(emailKey);
    }
}
