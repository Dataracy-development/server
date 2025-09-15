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
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    @NotBlank(message = "Swagger title은 필수입니다.")
    private String title;

    @NotBlank(message = "Swagger description은 필수입니다.")
    private String description;

    @NotBlank(message = "Swagger version은 필수입니다.")
    private String version;

    @NotBlank(message = "Swagger serverDescription은 필수입니다.")
    private String serverDescription;
}
