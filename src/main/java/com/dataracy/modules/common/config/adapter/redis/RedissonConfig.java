package com.dataracy.modules.common.config.adapter.redis;

import com.dataracy.modules.common.config.properties.RedissonProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 분산락 처리를 위한 레디슨 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    private final RedissonProperties properties;

    /**
     * Redis 서버에 연결된 RedissonClient 인스턴스를 생성하여 반환합니다.
     *
     * 이 메서드는 단일 Redis 서버에 대한 연결 정보를 기반으로 Redisson 클라이언트를 구성하며,
     * 분산 락 등 Redisson의 기능을 사용할 수 있도록 Spring Bean으로 등록합니다.
     *
     * @return 구성된 RedissonClient 인스턴스
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("rediss://" + properties.getHost() + ":" + properties.getPort())
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setConnectTimeout(3000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        return Redisson.create(config);
    }
}
