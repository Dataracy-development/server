package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataSimilarSearchPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticDataSimilarSearchAdapter implements DataSimilarSearchPort {

    private final ElasticsearchClient client;

    @Override
    public List<DataSimilarSearchResponse> recommendSimilarDataSets(Data data, int size) {
        try {
            SearchResponse<DataSearchDocument> response = client.search(s -> s
                            .index("data_index")
                            .size(size)
                            .sort(sort -> sort
                                    .field(f -> f.field("createdAt").order(SortOrder.Desc))
                            )
                            .query(q -> buildRecommendationQuery(q, data)),
                    DataSearchDocument.class
            );

            return response.hits().hits().stream()
                    .map(hit -> mapToSimilarResponse(hit.source()))
                    .toList();

        } catch (IOException e) {
            log.error("유사 데이터셋 조회 실패: dataId={}, size={}", data.getId(), size, e);
            throw new DataException(DataErrorStatus.FAIL_SIMILAR_SEARCH_DATA);
        }
    }

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
        );
    }

    private DataSimilarSearchResponse mapToSimilarResponse(DataSearchDocument doc) {
        return new DataSimilarSearchResponse(
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
                doc.recentWeekDownloadCount(),
                doc.rowCount(),
                doc.columnCount(),
                doc.createdAt()
        );
    }
}
