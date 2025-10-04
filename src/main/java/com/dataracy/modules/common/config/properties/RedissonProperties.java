package com.dataracy.modules.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedissonProperties {
  @NotBlank(message = "Redis host는 필수입니다.")
  private String host;

  @Min(value = 1, message = "Port는 1 이상이어야 합니다.")
  @Max(value = 65535, message = "Port는 65535 이하여야 합니다.")
  private int port;

  @NotBlank(message = "Redis protocol은 필수입니다.")
  private String protocol;
}
