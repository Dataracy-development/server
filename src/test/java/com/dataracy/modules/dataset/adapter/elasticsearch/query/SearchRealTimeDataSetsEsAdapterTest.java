package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchRealTimeDataSetsPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SearchRealTimeDataSetsEsAdapterTest {

    @Mock
    private ElasticsearchClient client;

    private SearchRealTimeDataSetsPort adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchRealTimeDataSetsEsAdapter(client);
    }

    @Test
    void searchRealTimeDataSetsShouldReturnMappedResults() throws Exception {
        // given: mock DataSearchDocument
        LocalDateTime createdAt = LocalDateTime.ofInstant(
                Instant.parse("2024-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );

        DataSearchDocument doc = DataSearchDocument.builder()
                .id(1L)
                .title("Test Data")
                .dataThumbnailUrl("thumb.png")
                .createdAt(createdAt) // ✅ LocalDateTime
                .build();

        Hit<DataSearchDocument> hit = new Hit.Builder<DataSearchDocument>()
                .id("1")
                .index("data_index")
                .source(doc)
                .build();

        SearchResponse<DataSearchDocument> mockResponse = SearchResponse.of(r -> r
                .took(1)
                .timedOut(false)
                .shards(s -> s.total(1).successful(1).failed(0))
                .hits(h -> h.hits(List.of(hit)))
        );

        given(client.search(
                any(Function.class),
                eq(DataSearchDocument.class))
        ).willReturn(mockResponse);

        // when
        List<RecentMinimalDataResponse> results = adapter.searchRealTimeDataSets("Test", 5);

        // then
        assertThat(results).hasSize(1);
        RecentMinimalDataResponse res = results.get(0);
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.title()).isEqualTo("Test Data");
        assertThat(res.dataThumbnailUrl()).isEqualTo("thumb.png");
        assertThat(res.createdAt()).isEqualTo(createdAt); // ✅ LocalDateTime 비교
    }

    @Test
    void searchRealTimeDataSetsShouldThrowDataExceptionOnIoError() throws Exception {
        // given
        given(client.search(
                any(Function.class),
                eq(DataSearchDocument.class))
        ).willThrow(new IOException("ES failure"));

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.searchRealTimeDataSets("Test", 5),
                DataException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.FAIL_REAL_TIME_SEARCH_DATASET);
    }
}
