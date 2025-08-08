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

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setConnectTimeout(3000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        return Redisson.create(config);
    }
}
