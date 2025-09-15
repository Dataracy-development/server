package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaStringProducerConfig extends AbstractKafkaProducerConfig<String> {

    /**
     * bootstrap-servers 유효성 체크
     */
    @PostConstruct
    public void validate() {
        validateBootstrapServers();
    }

    /**
     * key 직렬화: String
     */
    @Override
    protected Class<?> keySerializer() {
        return StringSerializer.class;
    }

    /**
     * value 직렬화: String
     */
    @Override
    protected Class<?> valueSerializer() {
        return StringSerializer.class;
    }

    /**
     * String → String 템플릿
     * (AbstractKafkaProducerConfig의 base 옵션: acks=all, enable.idempotence=true,
     *  max.in.flight=1, retries/timeout/linger/compression 등 적용)
     */
    @Bean(name = "stringKafkaTemplate")
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        KafkaTemplate<String, String> tpl = new KafkaTemplate<>(producerFactory());
        // (선택) Micrometer 관측 활성화: 메트릭 수집 시 사용
        // tpl.setObservationEnabled(true);
        return tpl;
    }
}
