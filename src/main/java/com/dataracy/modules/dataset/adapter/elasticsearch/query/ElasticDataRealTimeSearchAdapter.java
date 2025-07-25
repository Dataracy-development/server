package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataRealTimeSearchPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticDataRealTimeSearchAdapter implements DataRealTimeSearchPort {

    private final ElasticsearchClient client;

    /**
     * 주어진 키워드로 Elasticsearch에서 삭제되지 않은 데이터셋을 실시간으로 검색하여 최소 정보 응답 목록을 반환합니다.
     *
     * 키워드는 "title"(가중치 2) 및 "description" 필드에 대해 자동 퍼지 멀티매치 쿼리로 검색되며,
     * "isDeleted"가 false인 데이터만 포함됩니다. 결과는 "createdAt" 기준 내림차순으로 정렬되고, 요청한 개수만큼 제한됩니다.
     *
     * @param keyword 검색할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 검색된 데이터셋의 최소 정보 응답 객체 리스트
     * @throws DataException 실시간 데이터셋 검색에 실패한 경우
     */
    @Override
    public List<DataMinimalSearchResponse> search(String keyword, int size) {
        try {
            SearchResponse<DataSearchDocument> response = client.search(s -> s
                            .index("data_index")
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
                                                            .fields("title^2", "description")
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
                    DataSearchDocument.class
            );
            return response.hits().hits().stream()
                    .map(hit -> {
                        var doc = hit.source();
                        return new DataMinimalSearchResponse(doc.id(), doc.title(), doc.thumbnailUrl(), doc.createdAt());
                    })
                    .toList();

        } catch (IOException e) {
            log.error("데이터셋 실시간 검색 실패: keyword={}, size={}", keyword, size, e);
            throw new DataException(DataErrorStatus.FAIL_REAL_TIME_SEARCH_DATASET);
        }
    }
}
