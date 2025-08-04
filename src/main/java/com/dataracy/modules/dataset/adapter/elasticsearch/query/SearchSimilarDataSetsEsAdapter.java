package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchSimilarDataSetsPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchSimilarDataSetsEsAdapter implements SearchSimilarDataSetsPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "data_index";

    /**
     * 주어진 데이터와 유사한 데이터셋을 Elasticsearch에서 검색하여 추천 결과 목록을 반환합니다.
     *
     * @param data 유사성을 기준으로 검색할 기준 데이터 객체
     * @param size 반환할 추천 데이터셋의 최대 개수
     * @return 유사 데이터셋 추천 결과의 리스트
     * @throws DataException Elasticsearch 검색 중 오류가 발생한 경우
     */
    @Override
    public List<SimilarDataResponse> searchSimilarDataSets(Data data, int size) {
        try {
            Instant startTime = LoggerFactory.elastic().logQueryStart(INDEX, "유사 데이터셋 검색 시작: dataId=" + data.getId() + ", size=" + size);
            SearchResponse<DataSearchDocument> response = client.search(s -> s
                            .index(INDEX)
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f.field("createdAt").order(SortOrder.Desc))
                            )
                            .query(q -> buildRecommendationQuery(q, data)),
                    DataSearchDocument.class
            );
            List<SimilarDataResponse> similarDataResponses = response.hits().hits().stream()
                    .map(hit -> mapToSimilarResponse(hit.source()))
                    .toList();
            LoggerFactory.elastic().logQueryEnd(INDEX, "유사 데이터셋 검색 종료: dataId=" + data.getId() + ", size=" + size, startTime);
            return similarDataResponses;
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "유사 데이터셋 조회 실패: dataId=" + data.getId() + ", size=" + size, e);
            throw new DataException(DataErrorStatus.FAIL_SIMILAR_SEARCH_DATA);
        }
    }

    /**
     * 주어진 데이터와 유사한 데이터를 찾기 위한 Elasticsearch 쿼리를 생성합니다.
     *
     * 이 쿼리는 제목과 설명을 기반으로 한 more_like_this 조건, topicId에 대한 가중치(term) 조건, 동일한 id의 문서 제외, 그리고 삭제되지 않은(isDeleted=false) 문서만을 대상으로 하는 필터를 포함합니다.
     *
     * @param q    쿼리 빌더
     * @param data 유사 데이터 추천 기준이 되는 데이터 객체
     * @return 유사 데이터 검색을 위한 Elasticsearch 쿼리 빌더
     */
    private ObjectBuilder<Query> buildRecommendationQuery(Query.Builder q, Data data) {
        return q.bool(b -> b
                .should(sb -> sb.moreLikeThis(mlt -> mlt
                        .fields("title", "content")
                        .like(like -> like.text(data.getTitle() + " " + data.getDescription()))
                        .minTermFreq(1)
                        .minDocFreq(1)
                ))
                .should(sb -> sb.term(t -> t
                        .field("topicId")
                        .value(data.getTopicId())
                        .boost(1.5f)
                ))
                .mustNot(mn -> mn.term(t -> t
                        .field("id")
                        .value(data.getId())
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
     * DataSearchDocument 객체를 DataSimilarSearchResponse 객체로 변환합니다.
     *
     * @param doc 변환할 DataSearchDocument 객체
     * @return 변환된 DataSimilarSearchResponse 객체
     */
    private SimilarDataResponse mapToSimilarResponse(DataSearchDocument doc) {
        return new SimilarDataResponse(
                doc.id(),
                doc.title(),
                doc.topicLabel(),
                doc.dataSourceLabel(),
                doc.dataTypeLabel(),
                doc.startDate(),
                doc.endDate(),
                doc.description(),
                doc.thumbnailUrl(),
                doc.downloadCount(),
                doc.rowCount(),
                doc.columnCount(),
                doc.createdAt()
        );
    }
}
