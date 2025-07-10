package com.dataracy.modules.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.dataracy.modules.behaviorlog.adapter.elasticsearch.ElasticsearchConnectionProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchClientConfig {
    private final ElasticsearchConnectionProperties elasticsearchProperties;

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        return RestClient.builder(
                        new HttpHost(
                                elasticsearchProperties.getHost(),
                                Integer.parseInt(elasticsearchProperties.getPort()),
                                elasticsearchProperties.getProtocol()))
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
        // ✅ JavaTimeModule 등록
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);
        return new ElasticsearchClient(new RestClientTransport(restClient, jsonpMapper));
    }
}
