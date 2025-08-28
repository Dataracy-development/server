package com.dataracy.modules.project.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.port.out.query.search.SearchRealTimeProjectsPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SearchRealTimeProjectsEsAdapter implements SearchRealTimeProjectsPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
         * 주어진 키워드로 Elasticsearch 인덱스("project_index")에서 삭제되지 않은 프로젝트를 검색하여 실시간 응답 목록을 반환합니다.
         *
         * 멀티 매치 쿼리는 title에 가중치 3, username에 가중치 2를 적용하고 fuzziness="AUTO"를 사용합니다. 검색 결과는 createdAt 기준 내림차순으로 정렬되며 isDeleted가 true인 문서는 필터링됩니다. 반환되는 각 항목은 문서의 id, title, userId, username, projectThumbnailUrl를 포함한 RealTimeProjectResponse로 매핑됩니다.
         *
         * @param keyword 검색어(멀티 매치 쿼리에 사용)
         * @param size    반환할 최대 결과 수
         * @return 검색된 프로젝트 정보를 담은 RealTimeProjectResponse 리스트
         * @throws ProjectException 실시간 프로젝트 검색(Elasticsearch 호출) 실패 시 발생
         */
    @Override
    public List<RealTimeProjectResponse> searchByKeyword(String keyword, int size) {
        try {
            Instant startTime = LoggerFactory.elastic().logQueryStart(INDEX, "프로젝트 검색어 자동완성 실시간 검색 시작: keyword=" + keyword + ", size=" + size);
            SearchResponse<ProjectSearchDocument> response = client.search(s -> s
                            .index(INDEX)
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field("createdAt").order(SortOrder.Desc)
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

            List<RealTimeProjectResponse> realTimeProjectResponses = response.hits().hits().stream()
                    .map(hit -> {
                        ProjectSearchDocument doc = Objects.requireNonNull(hit.source(), "검색된 document가 null값입니다.");
                        return new RealTimeProjectResponse(
                                doc.id(),
                                doc.title(),
                                doc.userId(),
                                doc.username(),
                                doc.projectThumbnailUrl()
                        );
                    })
                    .toList();
            LoggerFactory.elastic().logQueryEnd(INDEX, "프로젝트 검색어 자동완성 실시간 검색 종료: keyword=" + keyword + ", size=" + size, startTime);
            return realTimeProjectResponses;
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 실시간 검색 실패: keyword=" + keyword + ", size=" + size, e);
            throw new ProjectException(ProjectErrorStatus.FAIL_REAL_TIME_SEARCH_PROJECT);
        }
    }
}
