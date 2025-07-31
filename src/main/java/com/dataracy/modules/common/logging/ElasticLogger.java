package com.dataracy.modules.common.logging;

public class ElasticLogger extends BaseLogger {

    public void logIndex(String index, String doc, String message) {
        info("[Elasticsearch 인덱싱] index={} doc={} message={}", index, doc, message);
    }

    public void logUpdate(String index, String message) {
        info("[Elasticsearch 업데이트] index={} message={}", index, message);
    }

    public void logSearch(String index, String query, String message) {
        debug("[Elasticsearch 검색] index={} query={} message={}", index, query, message);
    }

    public void logError(String index, String message, Throwable e) {
        error(e, "[Elasticsearch 오류] index={} message={}", index, message);
    }
}
