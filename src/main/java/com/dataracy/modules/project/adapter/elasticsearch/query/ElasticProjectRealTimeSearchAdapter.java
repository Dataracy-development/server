package com.dataracy.modules.project.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectRealTimeSearchPort;
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
public class ElasticProjectRealTimeSearchAdapter implements ProjectRealTimeSearchPort {

    private final ElasticsearchClient client;

    /**
     * 주어진 키워드로 Elasticsearch의 "project_index"에서 삭제되지 않은 프로젝트를 실시간으로 검색합니다.
     *
     * 검색 결과는 생성일(createdAt) 기준 내림차순으로 정렬되며, title과 username 필드에 가중치와 자동 퍼지(fuzziness)가 적용된 멀티 매치 쿼리를 사용합니다. 삭제된 프로젝트(isDeleted=true)는 결과에서 제외됩니다.
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
                            .index("project_index")
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field("createdAt")
                                            .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                                    )
                            )
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .multiMatch(mm -> mm
                                                            .fields("title^3", "username^2")
                                                            .query(keyword)
                                                            .fuzziness("AUTO")
                                                    )
                                            )
                                            .filter(f -> f
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(false)
                                                    )
                                            )
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
