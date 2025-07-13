package com.dataracy.modules.common.config;

import jakarta.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 분산락 처리를 위한 레디슨 설정 클래스
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:}")
    private String redisHost;

    @Value("${spring.data.redis.port:0}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }

    @PostConstruct
    public void validateProperties() {
        if (redisHost.isBlank()) {
            throw new IllegalStateException("Redis host 설정이 누락되었습니다. spring.data.redis.host 값을 확인해주세요.");
        }
        if (redisPort <= 0 || redisPort > 65535) {
            throw new IllegalStateException("Redis 포트 설정이 올바르지 않습니다. 유효한 포트 범위(1-65535)를 설정해주세요. 현재 값: " + redisPort);
        }
    }
}
