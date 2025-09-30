package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataFilterWebMapperTest {

    private DataFilterWebMapper dataFilterWebMapper;

    @BeforeEach
    void setUp() {
        dataFilterWebMapper = new DataFilterWebMapper();
    }

    @Test
    @DisplayName("toApplicationDto - FilteringDataWebRequest를 FilteringDataRequest로 변환한다")
    void toApplicationDto_WhenFilteringDataWebRequest_ConvertsToFilteringDataRequest() {
        // given
        FilteringDataWebRequest webRequest = new FilteringDataWebRequest(
                "AI",
                "LATEST",
                1L,
                2L,
                3L,
                2024
        );

        // when
        FilteringDataRequest result = dataFilterWebMapper.toApplicationDto(webRequest);

        // then
        assertThat(result.keyword()).isEqualTo("AI");
        assertThat(result.sortType()).isEqualTo("LATEST");
        assertThat(result.topicId()).isEqualTo(1L);
        assertThat(result.dataSourceId()).isEqualTo(2L);
        assertThat(result.dataTypeId()).isEqualTo(3L);
        assertThat(result.year()).isEqualTo(2024);
    }

    @Test
    @DisplayName("toApplicationDto - FilteringDataWebRequest의 모든 필드가 null인 경우에도 변환한다")
    void toApplicationDto_WhenAllFieldsAreNull_ConvertsToFilteringDataRequest() {
        // given
        FilteringDataWebRequest webRequest = new FilteringDataWebRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );

        // when
        FilteringDataRequest result = dataFilterWebMapper.toApplicationDto(webRequest);

        // then
        assertThat(result.keyword()).isNull();
        assertThat(result.sortType()).isNull();
        assertThat(result.topicId()).isNull();
        assertThat(result.dataSourceId()).isNull();
        assertThat(result.dataTypeId()).isNull();
        assertThat(result.year()).isNull();
    }

    @Test
    @DisplayName("toWebDto - FilteredDataResponse를 FilteredDataWebResponse로 변환한다")
    void toWebDto_WhenFilteredDataResponse_ConvertsToFilteredDataWebResponse() {
        // given
        FilteredDataResponse responseDto = new FilteredDataResponse(
                1L,
                "AI Dataset",
                1L,
                "Test User",
                "http://example.com/profile.jpg",
                "AI",
                "Government",
                "CSV",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "This is a test dataset",
                "http://example.com/thumbnail.jpg",
                100,
                1024L,
                1000,
                10,
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                5L
        );

        // when
        FilteredDataWebResponse result = dataFilterWebMapper.toWebDto(responseDto);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("AI Dataset");
        assertThat(result.creatorId()).isEqualTo(1L);
        assertThat(result.creatorName()).isEqualTo("Test User");
        assertThat(result.userProfileImageUrl()).isEqualTo("http://example.com/profile.jpg");
        assertThat(result.topicLabel()).isEqualTo("AI");
        assertThat(result.dataSourceLabel()).isEqualTo("Government");
        assertThat(result.dataTypeLabel()).isEqualTo("CSV");
        assertThat(result.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(result.description()).isEqualTo("This is a test dataset");
        assertThat(result.dataThumbnailUrl()).isEqualTo("http://example.com/thumbnail.jpg");
        assertThat(result.downloadCount()).isEqualTo(100);
        assertThat(result.sizeBytes()).isEqualTo(1024L);
        assertThat(result.rowCount()).isEqualTo(1000);
        assertThat(result.columnCount()).isEqualTo(10);
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertThat(result.countConnectedProjects()).isEqualTo(5L);
    }

    @Test
    @DisplayName("toWebDto - FilteredDataResponse의 모든 필드가 null인 경우에도 변환한다")
    void toWebDto_WhenAllFieldsAreNull_ConvertsToFilteredDataWebResponse() {
        // given
        FilteredDataResponse responseDto = new FilteredDataResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // when
        FilteredDataWebResponse result = dataFilterWebMapper.toWebDto(responseDto);

        // then
        assertThat(result.id()).isNull();
        assertThat(result.title()).isNull();
        assertThat(result.creatorId()).isNull();
        assertThat(result.creatorName()).isNull();
        assertThat(result.userProfileImageUrl()).isNull();
        assertThat(result.topicLabel()).isNull();
        assertThat(result.dataSourceLabel()).isNull();
        assertThat(result.dataTypeLabel()).isNull();
        assertThat(result.startDate()).isNull();
        assertThat(result.endDate()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.dataThumbnailUrl()).isNull();
        assertThat(result.downloadCount()).isNull();
        assertThat(result.sizeBytes()).isNull();
        assertThat(result.rowCount()).isNull();
        assertThat(result.columnCount()).isNull();
        assertThat(result.createdAt()).isNull();
        assertThat(result.countConnectedProjects()).isNull();
    }
}