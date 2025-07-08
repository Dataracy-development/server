package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogRepositoryPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Elasticsearch 어댑터 - 행동 로그를 Elasticsearch에 저장하는 역할을 합니다.
 * 이 어댑터는 RepositoryPort의 구현체로, 도메인에서 정의한 로그 객체를 인덱싱 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogElasticsearchAdapter implements BehaviorLogRepositoryPort {

    private final ElasticsearchClient elasticsearchClient;

    // 캐시된 인덱스 이름 (ex : "behavior-logs-2025.07") -> 현재는 소규모 프로젝트이기에 월별 인덱스로 설정
    // volatile로 선언해서 멀티스레드 환경에서도 변경된 값을 다른 스레드가 즉시 볼 수 있도록 보장
    private volatile String cachedIndexName;

    // 캐시된 날짜 (오늘 날짜) -> 매번 요청마다 날짜를 계산하는 작업은 서버에 부하를 오게 할 수 있으므로 캐시로 최적화한다.
    // 매일 0시에 바뀌므로, 날짜가 바뀌었는지 체크해서 인덱스 캐시를 갱신할 때 사용
    private volatile LocalDate cachedDate;

    // 월별 인덱스 롤링 -> 추후 프로젝트 규무 확장 시 일별로 변환 예정
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
                    cachedDate = today;
                    cachedIndexName = "behavior-logs-" + DateTimeFormatter.ofPattern("yyyy.MM").format(today);
                }
            }
        }
        return cachedIndexName;
    }


    /**
     * 도메인에서 전달받은 BehaviorLog를 Elasticsearch에 저장합니다.
     * 인덱스는 일별로 롤링되며, 저장 실패 시에도 서비스는 영향을 받지 않도록 로그만 출력합니다.
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

    // 재시작을 할 수 있을 경우
    private boolean isRetryableError(Exception e) {
        return e instanceof ElasticsearchException &&
                (e.getMessage().contains("timeout") || e.getMessage().contains("connection"));
    }
}
