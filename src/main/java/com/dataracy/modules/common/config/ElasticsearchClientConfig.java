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
     * 리소스 누수 방지를 위하여 애플리케이션 종료시 연결을 닫는다
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

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
}
