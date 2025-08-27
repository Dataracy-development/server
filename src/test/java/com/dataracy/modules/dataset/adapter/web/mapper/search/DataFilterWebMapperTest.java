package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataFilterWebMapperTest {

    private final DataFilterWebMapper mapper = new DataFilterWebMapper();

    @Test
    @DisplayName("FilteringDataWebRequest → FilteringDataRequest 매핑 성공")
    void toApplicationDto_success() {
        // given
        FilteringDataWebRequest webReq = new FilteringDataWebRequest(
                "keyword", "LATEST", 10L, 20L, 30L, 2023
        );

        // when
        FilteringDataRequest result = mapper.toApplicationDto(webReq);

        // then
        assertThat(result.keyword()).isEqualTo("keyword");
        assertThat(result.sortType()).isEqualTo("LATEST");
        assertThat(result.topicId()).isEqualTo(10L);
        assertThat(result.dataSourceId()).isEqualTo(20L);
        assertThat(result.dataTypeId()).isEqualTo(30L);
        assertThat(result.year()).isEqualTo(2023);
    }

    @Test
    @DisplayName("FilteredDataResponse → FilteredDataWebResponse 매핑 성공")
    void toWebDtoSuccess() {
        // given
        FilteredDataResponse dto = new FilteredDataResponse(
                1L, "title", "topic", "source", "type",
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
                "desc", "thumb.png", 100, 200L, 300, 20,
                LocalDateTime.of(2023, 5, 5, 10, 0), 5L
        );

        // when
        FilteredDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.topicLabel()).isEqualTo("topic");
        assertThat(result.downloadCount()).isEqualTo(100L);
        assertThat(result.countConnectedProjects()).isEqualTo(5L);
    }
}
