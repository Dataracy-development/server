package com.dataracy.modules.email.infra.redis;

import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.status.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRedisManager {

    private final StringRedisTemplate redisTemplate;

    @Value("${aws.ses.expire-minutes}")
    private long EXPIRE_MINUTES;

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
     * 레디스에 저장된 코드와 클라이언트로부터 받은 코드를 비교한다.
     * @param email 이메일
     * @param code 클라이언트로부터 받은 입력 코드
     * @return
     */
    public boolean verifyCode(String email, String code) {
        String emailKey = getEmailKey(email);
        String savedCode = redisTemplate.opsForValue().get(emailKey);
        if (!code.equals(savedCode)) {
            log.warn("Email certification code mismatch for email: {}", email);
            return false;
        }
        return true;
    }

    private String getEmailKey(String email) {
        return "email:certification: " + email;
    }
}
