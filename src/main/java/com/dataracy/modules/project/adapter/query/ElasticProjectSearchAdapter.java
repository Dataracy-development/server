package com.dataracy.modules.project.adapter.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.project.adapter.index.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.port.query.ProjectSearchQueryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProjectSearchAdapter implements ProjectSearchQueryPort {

    private final ElasticsearchClient client;

    /**
     * 주어진 키워드로 Elasticsearch에서 프로젝트를 실시간으로 검색하여 결과 목록을 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 검색된 프로젝트 정보를 담은 ProjectRealTimeSearchResponse 리스트
     * @throws ProjectException 실시간 프로젝트 검색에 실패한 경우 발생
     */
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
            log.error("프로젝트 실시간 검색 실패: keyword={}, size={}", keyword, size, e);
            throw new ProjectException(ProjectErrorStatus.FAIL_REAL_TIME_SEARCH_PROJECT);
        }
    }
}
