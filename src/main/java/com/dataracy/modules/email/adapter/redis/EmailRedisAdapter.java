package com.dataracy.modules.email.adapter.redis;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.email.application.port.out.cache.CacheEmailPort;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 레디스를 이용해 이메일 인증 저장 및 조회
 */
@Component
@RequiredArgsConstructor
public class EmailRedisAdapter implements CacheEmailPort {
    private final StringRedisTemplate redisTemplate;

    @Value("${aws.ses.expire-minutes:5}")
    private long EXPIRE_MINUTES;

    /**
     * 이메일 인증키 생성
     * @param email 이메일
     * @param type 이메일 인증코드 전송 목적
     * @return 레디스 키
     */
    private String getEmailKey(String email, EmailVerificationType type) {
        return switch (type) {
            case SIGN_UP -> "email:verification:signup:" + email;
            case PASSWORD_SEARCH -> "email:verification:password:search:" + email;
            case PASSWORD_RESET -> "email:verification:password:reset:" + email;
        };
    }

    /**
     * 이메일 인증코드 저장
     * @param email 이메일
     * @param code 인증 코드
     * @param verificationType 이메일 인증 코드 전송 목적 타입
     */
    @Override
    public void saveCode(String email, String code, EmailVerificationType verificationType) {
        String emailKey = getEmailKey(email, verificationType);
        try {
            redisTemplate.opsForValue().set(emailKey, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
            LoggerFactory.redis().logSaveOrUpdate(emailKey, "해당 인증 코드를 저장하였습니다. email=" + email);
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(emailKey, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(emailKey, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 레디스에 저장된 인증코드를 추출한다.
     * @param email 이메일
     * @param code 클라이언트로부터 받은 입력 코드
     * @param verificationType 이메일 인증 코드 전송 목적 타입
     */
    @Override
    public String verifyCode(String email, String code, EmailVerificationType verificationType) {
        String emailKey = getEmailKey(email, verificationType);
        try {
            Instant startTime = LoggerFactory.redis().logQueryStart(emailKey, "이메일 인증 코드 조회 시작. email=" + email);
            String verificationCode = redisTemplate.opsForValue().get(emailKey);
            LoggerFactory.redis().logQueryEnd(emailKey, "이메일 인증 코드 조회 종료. email=" + email, startTime);
            return verificationCode;
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(emailKey, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(emailKey, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }

    /**
     * 해당 이메일 인증 코드를 레디스에서 삭제한다.
     *
     * @param email 이메일
     * @param verificationType 이메일 인증 코드 전송 목적 타입
     */
    @Override
    public void deleteCode(String email, EmailVerificationType verificationType) {
        String emailKey = getEmailKey(email, verificationType);
        try {
            redisTemplate.delete(emailKey);
            LoggerFactory.redis().logDelete(emailKey, "이메일 인증 코드 삭제. email=" + email);
        } catch (RedisConnectionFailureException e) {
            LoggerFactory.redis().logError(emailKey, "레디스 서버 연결에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.REDIS_CONNECTION_FAILURE);
        } catch (DataAccessException e) {
            LoggerFactory.redis().logError(emailKey, "네트워크 오류로 데이터 접근에 실패했습니다.", e);
            throw new CommonException(CommonErrorStatus.DATA_ACCESS_EXCEPTION);
        }
    }
}
