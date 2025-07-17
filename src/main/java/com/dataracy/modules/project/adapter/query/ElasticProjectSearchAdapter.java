package com.dataracy.modules.project.adapter.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.project.adapter.index.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.port.query.ProjectSearchQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticProjectSearchAdapter implements ProjectSearchQueryPort {

    private final ElasticsearchClient client;

    @Override
    public List<ProjectRealTimeSearchResponse> search(String keyword, int size) {
        try {
            SearchResponse<ProjectSearchDocument> response = client.search(s -> s
                            .index("project")
                            .size(size)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("title^3", "username^2")
                                            .query(keyword)
                                            .fuzziness("AUTO")
                                    )
                            ),
                    ProjectSearchDocument.class
            );
            return response.hits().hits().stream()
                    .map(hit -> {
                        var doc = hit.source();
                        return new ProjectRealTimeSearchResponse(doc.id(), doc.title(), doc.username(), doc.fileUrl());
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("프로젝트 실시간 검색 실패", e);
        }
    }
}
