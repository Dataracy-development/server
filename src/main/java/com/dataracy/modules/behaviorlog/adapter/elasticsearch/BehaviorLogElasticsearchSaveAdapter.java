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

    /**
     * BehaviorLog 객체를 Elasticsearch 인덱스에 저장합니다.
     *
     * @param behaviorLog 저장할 BehaviorLog 도메인 객체
     */
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

    /**
     * 현재 날짜를 기준으로 Elasticsearch 인덱스 이름을 생성하여 반환합니다.
     *
     * 인덱스 이름은 "behavior-logs-yyyy.MM" 형식으로 생성되며, 하루에 한 번만 갱신되어 캐시에 저장됩니다.
     *
     * @return 오늘 날짜에 해당하는 인덱스 이름
     */
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

    /**
         * 주어진 예외가 재시도 가능한지 여부를 판단합니다.
         *
         * 예외가 {@link ConnectException} 또는 {@link SocketTimeoutException}의 인스턴스이거나,
         * 예외 메시지에 "timeout", "connection", "unavailable" 키워드가 포함되어 있으면 true를 반환합니다.
         *
         * @param e 검사할 예외 객체
         * @return 재시도 가능한 경우 true, 그렇지 않으면 false
         */
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
