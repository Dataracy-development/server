package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogElasticsearchAdapter implements BehaviorLogRepositoryPort {

    private final CustomElasticsearchClient elasticsearchClient; // Elasticsearch RestHighLevelClient 래퍼

    private static final String INDEX = "behavior-logs-*"; // 날짜별 인덱스

    @Override
    public void updateUserIdByAnonymousId(String userId, String anonymousId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        var query = QueryBuilders.termQuery("anonymousId.keyword", anonymousId);
        var script = new Script(ScriptType.INLINE, "painless", "ctx._source.userId = params.userId", params);

        try {
            elasticsearchClient.updateByQuery(INDEX, query, script);
            log.info("✅ Elasticsearch 로그 병합 완료: anonymousId → userId, {} → {}", anonymousId, userId);
        } catch (Exception e) {
            log.error("❌ 로그 병합 실패: {}", e.getMessage(), e);
        }
    }
}
