package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * application.yml로부터 주입받은 elasticsearch 설정 값
 */
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchProperties {
    @NotBlank(message = "호스트는 빈 값 또는 null이 허용되지 않습니다.")
    private String host;

    @NotBlank(message = "포트는 빈 값 또는 null이 허용되지 않습니다.")
    private String port;

    @NotBlank(message = "프로토콜은 빈 값 또는 null이 허용되지 않습니다.")
    private String protocol;
}
