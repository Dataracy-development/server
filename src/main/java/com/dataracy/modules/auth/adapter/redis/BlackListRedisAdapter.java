package com.dataracy.modules.auth.adapter.redis;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.dataracy.modules.auth.application.port.out.token.BlackListTokenPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlackListRedisAdapter implements BlackListTokenPort {
  private final StringRedisTemplate redisTemplate;

  /**
   * 블랙리스트 키 값을 설정
   *
   * @param token 토큰
   * @return 블랙리스트 키
   */
  private String getBlackListKey(String token) {
    return "blacklist:" + token;
  }

  /**
   * 주어진 JWT 토큰을 Redis에 블랙리스트로 등록합니다.
   *
   * @param token 블랙리스트에 추가할 JWT 토큰
   * @param expirationMillis 토큰의 블랙리스트 유지 기간(밀리초)
   */
  public void setBlackListToken(String token, long expirationMillis) {
    redisTemplate
        .opsForValue()
        .set(getBlackListKey(token), "logout", Duration.ofMillis(expirationMillis));
    LoggerFactory.redis().logSaveOrUpdate(token, "블랙 리스트 처리를 위한 토큰 레디스 저장에 성공했습니다.");
  }

  /**
   * 주어진 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
   *
   * @param token 확인할 JWT 토큰
   * @return 토큰이 블랙리스트에 있으면 true, 아니면 false
   */
  public boolean isBlacklisted(String token) {
    boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(getBlackListKey(token)));
    if (isBlacklisted) {
      LoggerFactory.redis().logExist(token, "블랙리스트 토큰 확인");
    }
    return isBlacklisted;
  }
}
