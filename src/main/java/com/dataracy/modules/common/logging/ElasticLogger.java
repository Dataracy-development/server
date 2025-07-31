package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

public class ElasticLogger extends BaseLogger {

    public Instant logQueryStart(String index, String message) {
        debug("[Elasticsearch 조회 시작] index={} message={}", index, message);
        return Instant.now();
    }

    public void logQueryEnd(String index, String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        debug("[Elasticsearch 조회 완료] index={} message={} duration={}ms", index, message, durationMs);
    }

    public void logIndex(String index, String docId, String message) {
        info("[Elasticsearch 인덱싱] index={} docId={} message={}", index, docId, message);
    }

    public void logUpdate(String index, String docId, String message) {
        info("[Elasticsearch 업데이트] index={} docId={} message={}", index, docId, message);
    }

    public void logSearch(String index, String query, String message) {
        debug("[Elasticsearch 검색] index={} query={} message={}", index, query, message);
    }

    public void logError(String index, String message, Throwable e) {
        error(e, "[Elasticsearch 오류] index={} message={}", index, message);
    }
}
