package com.dataracy.modules.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchClientConfig {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    /**
     * Elasticsearch 클러스터에 연결할 수 있는 {@link ElasticsearchClient} 빈을 생성합니다.
     *
     * 애플리케이션 설정에서 지정한 호스트로 HTTP Rest 클라이언트를 구성하고,
     * 이를 기반으로 JSON 직렬화 매퍼와 함께 Elasticsearch Java 클라이언트를 반환합니다.
     *
     * @return 구성된 ElasticsearchClient 인스턴스
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(HttpHost.create(elasticsearchHost)).build();
        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
}
