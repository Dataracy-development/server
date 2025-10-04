package com.dataracy.modules.common.config.adapter.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Redis와의 통신을 설정하는 구성 클래스입니다. 주로 문자열 및 JSON 형태의 데이터를 처리합니다. 이 설정은 어플리케이션의 캐시 처리, 레디스 저장 및 검증 시
 * 사용됩니다.
 */
@Configuration
public class RedisConfig {
  /**
   * 문자열 키와 JSON 직렬화된 값을 처리하는 RedisTemplate 빈을 생성합니다. 주어진 ObjectMapper를 사용하여 값과 해시 값을 JSON 형식으로
   * 직렬화하며, 키와 해시 키는 문자열로 직렬화합니다.
   *
   * @return 문자열 키와 객체 값을 위한 RedisTemplate 인스턴스
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Key: String, Value: JSON 직렬화
    StringRedisSerializer keySerializer = new StringRedisSerializer();
    GenericJackson2JsonRedisSerializer valueSerializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    template.setValueSerializer(valueSerializer);
    template.setHashValueSerializer(valueSerializer);

    return template;
  }

  // 문자열 저장 시
  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    return new StringRedisTemplate(connectionFactory);
  }
}
