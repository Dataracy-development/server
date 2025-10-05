package com.dataracy.config;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;

/** 통합 테스트용 설정 클래스 Redis 등 인프라 의존성을 모킹합니다. */
@TestConfiguration
@Profile("test")
public class IntegrationTestConfig {

  /** Redis Connection Factory 모킹 */
  @Bean
  @Primary
  public RedisConnectionFactory redisConnectionFactory() {
    return mock(RedisConnectionFactory.class);
  }

  /** Redis Template 모킹 */
  @Bean
  @Primary
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }

  /** String Redis Template 모킹 */
  @Bean
  @Primary
  public StringRedisTemplate stringRedisTemplate() {
    return mock(StringRedisTemplate.class);
  }

  /** Email Code Port 모킹 */
  @Bean
  @Primary
  public ManageEmailCodePort manageEmailCodePort() {
    return mock(ManageEmailCodePort.class);
  }

  /** Refresh Token Port 모킹 */
  @Bean
  @Primary
  public ManageRefreshTokenPort manageRefreshTokenPort() {
    return mock(ManageRefreshTokenPort.class);
  }
}
