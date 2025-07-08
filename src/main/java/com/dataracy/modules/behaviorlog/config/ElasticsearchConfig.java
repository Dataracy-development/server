package com.dataracy.modules.behaviorlog.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(
                        new org.apache.http.HttpHost("localhost", 9200, "http"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom()
                            .setSoKeepAlive(true)
                            .build());
                    return httpClientBuilder;
                });

        RestClient restClient = builder.build();

        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
}

