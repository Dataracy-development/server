package com.dataracy.modules.common.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Elasticsearch 접속 설정
 * application.yml의 elasticsearch.connection.* 에서 주입됨
 */
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "elasticsearch.connection")
public class ElasticsearchConnectionProperties {
    /**
     * 예: localhost
     */
    @NotBlank(message = "Elasticsearch host는 필수입니다.")
    private String host;

    /**
     * 예: 9200
     */
    @NotNull(message = "Elasticsearch port는 필수입니다.")
    @Min(value = 1, message = "Port는 1 이상이어야 합니다.")
    @Max(value = 65535, message = "Port는 65535 이하여야 합니다.")
    private Integer port;

    /**
     * 예: http
     */
    @NotBlank(message = "Elasticsearch protocol은 필수입니다.")
    private String protocol;
}
