package com.dataracy.modules.common.config.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.dataracy.modules.common.config.properties.ElasticsearchConnectionProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchClientConfig {
    private final ElasticsearchConnectionProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * Elasticsearch 서버에 연결하기 위한 RestClient 빈을 생성한다.
     *
     * 호스트, 포트, 프로토콜 등 설정 정보를 기반으로 연결을 구성하며,
     * 연결 및 소켓 타임아웃(각각 5000ms, 60000ms)과 최대 연결 수(총 100개, 라우트당 10개)를 지정한다.
     * 애플리케이션 종료 시 자동으로 연결이 닫혀 리소스 누수를 방지한다.
     *
     * @return 구성된 RestClient 인스턴스
     */
    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        return RestClient.builder(new HttpHost(properties.getHost(), properties.getPort(), properties.getProtocol()))
                .setRequestConfigCallback(requestConfig -> requestConfig
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000))
                .setHttpClientConfigCallback(httpClient -> httpClient
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(10))
                .build();
    }

    /**
     * 주어진 REST 클라이언트를 사용하여 Elasticsearch와 통신할 수 있는 ElasticsearchClient 빈을 생성합니다.
     *
     * @return Elasticsearch와의 REST API 통신을 지원하는 ElasticsearchClient 인스턴스
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);
        return new ElasticsearchClient(new RestClientTransport(restClient, jsonpMapper));
    }
}
