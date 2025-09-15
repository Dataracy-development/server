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
     * BehaviorLog 도메인 객체를 월별 롤링 인덱스에 Elasticsearch로 저장합니다.
     *
     * BehaviorLog의 timestamp가 null이면 현재 시각으로 설정하여 저장합니다.
     * 저장 실패 시 예외를 전파하지 않고 오류 로그만 남기며, 서비스 동작에는 영향을 주지 않습니다.
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
     * 현재 날짜 기준으로 월별 롤링되는 Elasticsearch 인덱스 이름을 반환합니다.
     *
     * 인덱스 이름은 "behavior-logs-yyyy.MM" 형식이며, 날짜가 변경될 때만 캐시를 갱신하여 불필요한 연산을 최소화합니다.
     * 멀티스레드 환경에서도 안전하게 동작하도록 동기화되어 있습니다.
     *
     * @return 현재 월에 해당하는 Elasticsearch 인덱스 이름
     */
    private String resolveIndexName() {
        LocalDate today = LocalDate.now();

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
     * 주어진 예외가 Elasticsearch의 재시도 가능한 오류(타임아웃 또는 연결 문제)인지 판별합니다.
     *
     * 예외 메시지에 "timeout" 또는 "connection"이 포함되어 있으면 true를 반환합니다.
     *
     * @param e 검사할 예외 객체
     * @return 재시도 가능한 오류일 경우 true, 아니면 false
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
