package com.dataracy;

import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
class TestConfig {
    @Bean
    public CurrentUserIdArgumentResolver currentUserIdArgumentResolver() {
        return new CurrentUserIdArgumentResolver();
    }
}

