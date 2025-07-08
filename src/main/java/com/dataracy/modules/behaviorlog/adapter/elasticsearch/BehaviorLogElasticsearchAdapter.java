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

    /**
     * 현재 월을 기준으로 "behavior-logs-yyyy.MM" 형식의 Elasticsearch 인덱스 이름을 생성합니다.
     *
     * @return 월별 롤링 인덱스 이름
     */
    private String getDailyRollingIndexName() {
        String dateSuffix = DateTimeFormatter.ofPattern("yyyy.MM").format(LocalDate.now());
        return "behavior-logs-" + dateSuffix;
    }

    /**
     * BehaviorLog 도메인 객체를 Elasticsearch에 저장합니다.
     *
     * BehaviorLog의 timestamp가 null인 경우 현재 시각으로 설정하여 저장하며, 월별 롤링 인덱스에 기록됩니다.
     * 저장 과정에서 예외가 발생해도 서비스 동작에는 영향을 주지 않으며, 오류는 로그로만 남깁니다.
     *
     * @param behaviorLog 저장할 BehaviorLog 도메인 객체
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
