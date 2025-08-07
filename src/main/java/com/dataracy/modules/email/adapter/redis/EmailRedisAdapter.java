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
     * 이메일 인증 코드를 Redis에 저장합니다.
     *
     * @param email 인증 코드를 저장할 대상 이메일 주소
     * @param code 저장할 인증 코드 값
     * @param verificationType 인증 코드의 용도(예: 회원가입, 비밀번호 찾기 등)
     * @throws CommonException Redis 연결 실패 또는 데이터 접근 오류 발생 시 예외가 발생합니다.
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
     * 지정한 이메일과 인증 타입에 해당하는 인증 코드를 레디스에서 조회하여 반환합니다.
     *
     * @param email 인증 코드를 조회할 이메일 주소
     * @param code 클라이언트가 입력한 인증 코드(비교 목적이 아닌 조회용)
     * @param verificationType 인증 코드의 용도(예: 회원가입, 비밀번호 찾기 등)
     * @return 레디스에 저장된 인증 코드 값, 존재하지 않으면 null 반환
     * @throws CommonException 레디스 연결 실패 또는 데이터 접근 오류 발생 시
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
     * 지정한 이메일과 인증 유형에 해당하는 인증 코드를 레디스에서 삭제합니다.
     *
     * @param email 인증 코드를 삭제할 이메일 주소
     * @param verificationType 인증 코드의 용도(예: 회원가입, 비밀번호 찾기 등)
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
