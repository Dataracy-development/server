package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchSimilarDataSetsPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
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
class SearchSimilarDataSetsEsAdapterTest {

    @Mock
    private ElasticsearchClient client;

    private SearchSimilarDataSetsPort adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchSimilarDataSetsEsAdapter(client);
    }

    @Test
    void searchSimilarDataSetsShouldReturnMappedResults() throws Exception {
        // given
        LocalDateTime createdAt = LocalDateTime.ofInstant(
                java.time.Instant.parse("2024-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );

        DataSearchDocument doc = DataSearchDocument.builder()
                .id(1L)
                .title("Similar Test")
                .topicLabel("TopicA")
                .dataSourceLabel("SourceA")
                .dataTypeLabel("TypeA")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .description("Desc")
                .dataThumbnailUrl("thumb.png")
                .downloadCount(100)
                .sizeBytes(1024L)
                .rowCount(50)
                .columnCount(10)
                .createdAt(createdAt)
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

        Data baseData = Data.builder()
                .id(99L)
                .title("Base Data")
                .description("Base Desc")
                .topicId(10L)
                .build();

        // when
        List<SimilarDataResponse> results = adapter.searchSimilarDataSets(baseData, 5);

        // then
        assertThat(results).hasSize(1);
        SimilarDataResponse res = results.get(0);
        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.title()).isEqualTo("Similar Test");
        assertThat(res.topicLabel()).isEqualTo("TopicA");
        assertThat(res.dataSourceLabel()).isEqualTo("SourceA");
        assertThat(res.dataTypeLabel()).isEqualTo("TypeA");
        assertThat(res.startDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(res.endDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(res.description()).isEqualTo("Desc");
        assertThat(res.dataThumbnailUrl()).isEqualTo("thumb.png");
        assertThat(res.downloadCount()).isEqualTo(100L);
        assertThat(res.sizeBytes()).isEqualTo(1024L);
        assertThat(res.rowCount()).isEqualTo(50L);
        assertThat(res.columnCount()).isEqualTo(10L);
        assertThat(res.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void searchSimilarDataSetsShouldThrowDataExceptionOnIoError() throws Exception {
        // given
        Data baseData = Data.builder()
                .id(99L)
                .title("Base Data")
                .description("Base Desc")
                .topicId(10L)
                .build();

        given(client.search(
                any(Function.class),
                eq(DataSearchDocument.class))
        ).willThrow(new IOException("ES failure"));

        // when
        DataException thrown = catchThrowableOfType(
                () -> adapter.searchSimilarDataSets(baseData, 5),
                DataException.class
        );

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(DataErrorStatus.FAIL_SIMILAR_SEARCH_DATA);
    }
}
