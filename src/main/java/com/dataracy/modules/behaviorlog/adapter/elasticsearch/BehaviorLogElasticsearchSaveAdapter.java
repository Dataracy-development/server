package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogElasticsearchSaveAdapter implements SaveBehaviorLogPort {

    private final ElasticsearchClient elasticsearchClient;

    private static final String INDEX_PREFIX = "behavior-logs-";
    private volatile String cachedIndexName;
    private volatile LocalDate cachedDate;

    @Override
    public void save(BehaviorLog behaviorLog) {
        try {
            String indexName = resolveIndexName();
            IndexRequest<BehaviorLog> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .document(behaviorLog)
            );

            IndexResponse response = elasticsearchClient.index(request);
            log.debug("BehaviorLog 저장 완료: id={}, result={}", response.id(), response.result());

        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패: {}", e.getMessage(), e);
            if (isRetryable(e)) {
                // retry logic or send to DLQ
            }
        }
    }

    private String resolveIndexName() {
        LocalDate today = LocalDate.now();

        // 단 한번만 진입하도록 설정
        if (cachedDate == null || !cachedDate.equals(today)) {
            synchronized (this) {
                if (cachedDate == null || !cachedDate.equals(today)) {
                    cachedIndexName = INDEX_PREFIX + DateTimeFormatter.ofPattern("yyyy.MM").format(today);
                    cachedDate = today;
                }
            }
        }
        return cachedIndexName;
    }

    private boolean isRetryable(Exception e) {
        // 예외 타입 기반 판단
        if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            return true;
        }
        // 메시지 기반 판단
        return Optional.ofNullable(e.getMessage())
              .map(msg -> msg.toLowerCase().contains("timeout") ||
                      msg.toLowerCase().contains("connection") ||
                      msg.toLowerCase().contains("unavailable"))
             .orElse(false);
        }
}
