package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
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

    // 월별 롤링 인덱스 이름 생성: behavior-logs-2025.07
    private String getDailyRollingIndexName() {
        String dateSuffix = DateTimeFormatter.ofPattern("yyyy.MM").format(LocalDate.now());
        return "behavior-logs-" + dateSuffix;
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
                behaviorLog = BehaviorLog.builder()
                        .userId(behaviorLog.getUserId())
                        .anonymousId(behaviorLog.getAnonymousId())
                        .path(behaviorLog.getPath())
                        .method(behaviorLog.getMethod())
                        .status(behaviorLog.getStatus())
                        .responseTime(behaviorLog.getResponseTime())
                        .userAgent(behaviorLog.getUserAgent())
                        .ip(behaviorLog.getIp())
                        .action(behaviorLog.getAction())
                        .dbLatency(behaviorLog.getDbLatency())
                        .externalLatency(behaviorLog.getExternalLatency())
                        .timestamp(Instant.now().toString())
                        .build();
            }

            BehaviorLog finalBehaviorLog = behaviorLog;
            IndexRequest<BehaviorLog> request = IndexRequest.of(i -> i
                    .index(getDailyRollingIndexName()) // 월별 인덱스
                    .document(finalBehaviorLog)
            );

            IndexResponse response = elasticsearchClient.index(request);
            log.debug("BehaviorLog 저장 완료: id={}, result={}", response.id(), response.result());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패: {}", e.getMessage(), e);
        }
    }
}
