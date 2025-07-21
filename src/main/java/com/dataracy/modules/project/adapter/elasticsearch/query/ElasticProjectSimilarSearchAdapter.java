package com.dataracy.modules.project.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectSimilarSearchPort;
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

    /**
     * 주어진 프로젝트와 유사한 프로젝트를 Elasticsearch에서 검색하여 추천 프로젝트 목록을 반환합니다.
     *
     * @param project 유사도를 판단할 기준 프로젝트
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사 프로젝트 추천 결과 리스트
     * @throws ProjectException Elasticsearch 검색에 실패한 경우 발생
     */
    @Override
    public List<ProjectSimilarSearchResponse> recommendSimilarProjects(Project project, int size) {
        try {
            SearchResponse<ProjectSearchDocument> response = client.search(s -> s
                            .index("project_index")
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f.field("createdAt").order(SortOrder.Desc))
                            )
                            .query(q -> buildRecommendationQuery(q, project)),
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
     * 주어진 프로젝트를 기반으로 유사 프로젝트 추천을 위한 Elasticsearch 쿼리를 생성합니다.
     *
     * 프로젝트의 제목과 내용을 활용한 유사 문서 검색, 주제 ID 및 분석 목적 ID의 가중치 부여, 
     * 그리고 동일 프로젝트 제외 조건을 포함한 불리언 쿼리를 구성합니다.
     *
     * @param q       쿼리 빌더 객체
     * @param project 유사 프로젝트 추천 기준이 되는 프로젝트
     * @return 유사 프로젝트 추천을 위한 Elasticsearch 쿼리 빌더
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
                        .boost(1.3f)
                ))
                .mustNot(mn -> mn.term(t -> t
                        .field("id")
                        .value(project.getId())
                ))
        );
    }

    /**
     * ProjectSearchDocument를 ProjectSimilarSearchResponse로 변환하여 프로젝트의 주요 정보와 함께 댓글 수, 좋아요 수, 조회 수를 포함합니다.
     *
     * @param doc 변환할 프로젝트 검색 문서
     * @return 프로젝트의 상세 정보와 통계가 포함된 유사 프로젝트 응답 객체
     */
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
                doc.authorLevelLabel(),
                doc.commentCount(),
                doc.likeCount(),
                doc.viewCount()
        );
    }
}
