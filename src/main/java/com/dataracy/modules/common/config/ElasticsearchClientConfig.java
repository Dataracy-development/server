package com.dataracy.modules.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.dataracy.modules.behaviorlog.adapter.elasticsearch.ElasticsearchConnectionProperties;
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
     * Elasticsearch 서버에 연결하기 위한 RestClient 인스턴스를 생성하여 빈으로 등록합니다.
     *
     * 연결 호스트, 포트, 프로토콜은 설정 프로퍼티에서 가져오며, 연결 및 소켓 타임아웃과 최대 연결 수를 설정합니다.
     *
     * @return 구성된 Elasticsearch RestClient 인스턴스
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
     * Elasticsearch Java 클라이언트를 생성하여 빈으로 등록합니다.
     *
     * @param restClient Elasticsearch REST 클라이언트 인스턴스
     * @return 고수준 ElasticsearchClient 인스턴스
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);
        return new ElasticsearchClient(new RestClientTransport(restClient, jsonpMapper));
    }
}
