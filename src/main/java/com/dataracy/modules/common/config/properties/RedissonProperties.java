package com.dataracy.modules.common.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedissonProperties {
    @NotBlank(message = "Redis host는 필수입니다.")
    private String host;

    @NotBlank(message = "Redis port는 필수입니다.")
    private int port;
}
