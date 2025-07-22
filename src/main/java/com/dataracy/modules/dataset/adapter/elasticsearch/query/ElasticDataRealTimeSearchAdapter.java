package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataRealTimeSearchPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
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
public class ElasticDataRealTimeSearchAdapter implements DataRealTimeSearchPort {

    private final ElasticsearchClient client;

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
                                    .multiMatch(m -> m
                                            .fields("title^2", "description")
                                            .query(keyword)
                                            .fuzziness("AUTO")
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
