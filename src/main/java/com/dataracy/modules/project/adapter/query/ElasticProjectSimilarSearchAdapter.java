package com.dataracy.modules.project.adapter.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.dataracy.modules.project.adapter.index.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.application.port.query.ProjectSimilarSearchPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProjectSimilarSearchAdapter implements ProjectSimilarSearchPort {

    private final ElasticsearchClient client;

    @Override
    public List<ProjectSimilarSearchResponse> recommendSimilarProjects(Project project, int size) {
        try {
            SearchResponse<ProjectSearchDocument> response = client.search(s -> s
                            .index("project")
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f.field("createdAt").order(SortOrder.Desc))
                            )
                            .query(q -> buildRecommendationQuery(q, project)), // 💡 핵심 수정
                    ProjectSearchDocument.class
            );

            return response.hits().hits().stream()
                    .map(hit -> mapToSimilarResponse(hit.source()))
                    .toList();

        } catch (IOException e) {
            log.error("유사 프로젝트 조회 실패: projectId={}, size={}", project.getId(), size, e);
            throw new ProjectException(ProjectErrorStatus.FAIL_SIMILAR_SEARCH_PROJECT);
        }
    }

    /**
     * 유사도 검색 쿼리 구성
     */
    private ObjectBuilder<Query> buildRecommendationQuery(Query.Builder q, Project project) {
        return q.bool(b -> b
                .should(sb -> sb.moreLikeThis(mlt -> mlt
                        .fields("title", "content")
                        .like(like -> like.text(project.getTitle() + " " + project.getContent()))
                        .minTermFreq(1)
                        .minDocFreq(1)
                ))
                .should(sb -> sb.term(t -> t
                        .field("topicId")
                        .value(project.getTopicId())
                        .boost(1.5f)
                ))
                .should(sb -> sb.term(t -> t
                        .field("analysisPurposeId")
                        .value(project.getAnalysisPurposeId())
                        .boost(1.2f)
                ))
                .mustNot(mn -> mn.term(t -> t
                        .field("id")
                        .value(project.getId())
                ))
        );
    }

    private ProjectSimilarSearchResponse mapToSimilarResponse(ProjectSearchDocument doc) {
        return new ProjectSimilarSearchResponse(
                doc.id(),
                doc.title(),
                doc.content(),
                doc.username(),
                doc.fileUrl(),
                doc.topicLabel(),
                doc.analysisPurposeLabel(),
                doc.dataSourceLabel(),
                doc.authorLevelLabel()
        );
    }
}
