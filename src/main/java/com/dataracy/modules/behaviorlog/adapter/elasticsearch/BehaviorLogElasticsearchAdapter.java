package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Elasticsearch 어댑터 - 행동 로그를 Elasticsearch에 저장하는 역할을 합니다.
 * 이 어댑터는 RepositoryPort의 구현체로, 도메인에서 정의한 로그 객체를 인덱싱 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogElasticsearchAdapter implements SaveBehaviorLogPort {

    private final ElasticsearchClient elasticsearchClient;

    // 캐시된 인덱스 이름 (ex : "behavior-logs-2025.07") -> 현재는 소규모 프로젝트이기에 월별 인덱스로 설정
    // volatile로 선언해서 멀티스레드 환경에서도 변경된 값을 다른 스레드가 즉시 볼 수 있도록 보장
    private volatile String cachedIndexName;

    // 캐시된 날짜 (오늘 날짜) -> 매번 요청마다 날짜를 계산하는 작업은 서버에 부하를 오게 할 수 있으므로 캐시로 최적화한다.
    // 매일 0시에 바뀌므로, 날짜가 바뀌었는지 체크해서 인덱스 캐시를 갱신할 때 사용
    private volatile LocalDate cachedDate;

    /**
     * 현재 월에 해당하는 Elasticsearch 인덱스 이름을 반환합니다.
     *
     * 인덱스 이름은 "behavior-logs-yyyy.MM" 형식이며, 날짜가 변경될 때만 캐시를 갱신하여 성능을 최적화합니다.
     * 멀티스레드 환경에서 안전하게 동작하도록 동기화 처리되어 있습니다.
     *
     * @return 월별 롤링되는 Elasticsearch 인덱스 이름
     */
    private String getMonthlyRollingIndexName() {
        // 오늘 날짜를 구함
        LocalDate today = LocalDate.now();

        // 캐시된 날짜가 없거나(처음 실행), 오늘과 다르면 인덱스 캐시를 갱신해야 함
        if (cachedDate == null || !cachedDate.equals(today)) {

            // 멀티 스레드로 동시에 이 블록에 들어오게 될 경우
            // 한 번만 캐시를 갱신하도록 synchronized(this)로 동기화 (모니터 락)
            synchronized (this) {
                // 이중 체크: synchronized 안에서도 한번 더 확인 (다른 스레드가 먼저 갱신했을 수 있음) -> 첫 한 스레드만 락에 들어옴
                if (cachedDate == null || !cachedDate.equals(today)) {
                    cachedIndexName = "behavior-logs-" + DateTimeFormatter.ofPattern("yyyy.MM").format(today);
                    cachedDate = today;
                }
            }
        }
        return cachedIndexName;
    }


    /**
     * BehaviorLog 도메인 객체를 Elasticsearch에 저장합니다.
     *
     * BehaviorLog의 timestamp가 null인 경우 현재 시각으로 설정하여 저장합니다.
     * 저장 대상 인덱스는 월별로 롤링되는 인덱스입니다.
     * 저장 실패 시 예외를 전파하지 않고 오류 로그만 남기며, 서비스 동작에는 영향을 주지 않습니다.
     */
    @Override
    public void save(BehaviorLog behaviorLog) {
        try {
            // timestamp가 null일 경우 현재 시간으로 설정
            if (behaviorLog.getTimestamp() == null) {
                behaviorLog = behaviorLog.withTimestamp(Instant.now());
            }

            BehaviorLog finalBehaviorLog = behaviorLog;
            IndexRequest<BehaviorLog> request = IndexRequest.of(i -> i
                    .index(getMonthlyRollingIndexName()) // 월별 인덱스
                    .document(finalBehaviorLog)
            );

            IndexResponse response = elasticsearchClient.index(request);
            log.debug("BehaviorLog 저장 완료: id={}, result={}", response.id(), response.result());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패: {}", e.getMessage(), e);
            // 메트릭 수집 (예: Micrometer)
            // meterRegistry.counter("elasticsearch.save.failure").increment();
            // 중요한 오류의 경우 알림 발송 고려
            if (isRetryableError(e)) {
            // 재시도 로직 또는 DLQ로 전송
            }
        }
    }

    /**
     * 주어진 예외가 재시도 가능한 Elasticsearch 오류인지 여부를 반환합니다.
     *
     * 예외가 {@link ElasticsearchException} 이고 메시지에 "timeout" 또는 "connection"이 포함된 경우 true를 반환합니다.
     *
     * @param e 검사할 예외
     * @return 재시도 가능한 오류이면 true, 그렇지 않으면 false
     */
    private boolean isRetryableError(Exception e) {
        return e instanceof ElasticsearchException &&
                Optional.ofNullable(e.getMessage())
                    .map(msg -> msg.contains("timeout") || msg.contains("connection"))
                    .orElse(false);
    }
}
