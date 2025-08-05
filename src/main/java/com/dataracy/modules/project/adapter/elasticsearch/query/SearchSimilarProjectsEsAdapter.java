package com.dataracy.modules.project.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.application.port.out.query.search.SearchSimilarProjectsPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SearchSimilarProjectsEsAdapter implements SearchSimilarProjectsPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
     * 주어진 프로젝트와 유사한 프로젝트를 Elasticsearch에서 검색하여 추천 프로젝트 목록을 반환합니다.
     *
     * @param project 유사도를 판단할 기준이 되는 프로젝트
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사 프로젝트 추천 결과 리스트
     * @throws ProjectException Elasticsearch 검색에 실패한 경우 발생
     */
    @Override
    public List<SimilarProjectResponse> searchSimilarProjects(Project project, int size) {
        try {
            Instant startTime = LoggerFactory.elastic().logQueryStart(INDEX, "유사 프로젝트 검색 시작: projectId=" + project.getId() + ", size=" + size);
            SearchResponse<ProjectSearchDocument> response = client.search(s -> s
                            .index(INDEX)
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f.field("createdAt").order(SortOrder.Desc))
                            )
                            .query(q -> buildRecommendationQuery(q, project)),
                    ProjectSearchDocument.class
            );
            List<SimilarProjectResponse> similarProjectResponses = response.hits().hits().stream()
                    .map(hit -> mapToSimilarResponse(Objects.requireNonNull(hit.source(), "검색된 document가 null값입니다.")))
                    .toList();
            LoggerFactory.elastic().logQueryEnd(INDEX, "유사 프로젝트 검색 종료: projectId=" + project.getId() + ", size=" + size, startTime);
            return similarProjectResponses;
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "유사 프로젝트 조회 실패: projectId=" + project.getId() + ", size=" + size, e);
            throw new ProjectException(ProjectErrorStatus.FAIL_SIMILAR_SEARCH_PROJECT);
        }
    }

    /**
     * 주어진 프로젝트를 기준으로 유사 프로젝트 추천을 위한 Elasticsearch 불리언 쿼리를 생성합니다.
     *
     * 제목과 내용을 활용한 유사 문서 검색, 주제 ID 및 분석 목적 ID의 가중치 부여, 동일 프로젝트 제외, 삭제되지 않은 프로젝트만 포함하는 조건을 결합합니다.
     *
     * @param q 쿼리 빌더 객체
     * @param project 유사 프로젝트 추천의 기준이 되는 프로젝트
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
                .filter(f -> f
                        .term(t -> t
                                .field("isDeleted")
                                .value(false)
                        )
                )
        );
    }

    /**
     * 프로젝트 검색 문서를 유사 프로젝트 응답 객체로 변환합니다.
     *
     * ProjectSearchDocument의 주요 정보와 통계(댓글 수, 좋아요 수, 조회 수 등)를 SimilarProjectResponse로 매핑하여 반환합니다.
     *
     * @param doc 변환할 프로젝트 검색 문서
     * @return 유사 프로젝트의 상세 정보와 통계가 포함된 응답 객체
     */
    private SimilarProjectResponse mapToSimilarResponse(ProjectSearchDocument doc) {
        return new SimilarProjectResponse(
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
