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

    @Value("${elasticsearch.host:http://localhost:9200}")
    private String elasticsearchHost;

    /**
     * Elasticsearch 서버에 연결하기 위한 RestClient 빈을 생성한다.
     *
     * 연결 및 소켓 타임아웃, 최대 연결 수 등의 설정을 적용하며, 애플리케이션 종료 시 자동으로 연결이 닫혀 리소스 누수를 방지한다.
     *
     * @return 설정된 RestClient 인스턴스
     */
    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create(elasticsearchHost))
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(5000)
                                .setSocketTimeout(60000))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setMaxConnTotal(100)
                                .setMaxConnPerRoute(10))
                .build();
    }

    /**
     * Elasticsearch REST 클라이언트를 기반으로 ElasticsearchClient 인스턴스를 생성하여 빈으로 등록합니다.
     *
     * @return Elasticsearch와의 통신을 위한 ElasticsearchClient 인스턴스
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
}
