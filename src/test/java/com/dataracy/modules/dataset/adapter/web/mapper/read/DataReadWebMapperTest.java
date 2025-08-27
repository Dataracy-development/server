package com.dataracy.modules.dataset.adapter.web.mapper.read;

import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataReadWebMapperTest {

    private final DataReadWebMapper mapper = new DataReadWebMapper();

    @Test
    @DisplayName("DataDetailResponse → DataDetailWebResponse 매핑 성공")
    void toWebDtoFromDataDetailResponseSuccess() {
        // given
        DataDetailResponse dto = new DataDetailResponse(
                1L, "title", "userA", "profile.png", "intro",
                "author", "occupation", "topic", "source", "type",
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
                "desc", "guide", "thumb.png",
                100, 200L, 300, 10,
                "{\"col\":\"val\"}", LocalDateTime.of(2023, 5, 1, 10, 0)
        );

        // when
        DataDetailWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.username()).isEqualTo("userA");
        assertThat(result.downloadCount()).isEqualTo(100L);
        assertThat(result.previewJson()).contains("col");
    }

    @Test
    @DisplayName("DataGroupCountResponse → DataGroupCountWebResponse 매핑 성공")
    void toWebDtoFromDataGroupCountResponseSuccess() {
        // given
        DataGroupCountResponse dto = new DataGroupCountResponse(10L, "topicA", 5L);

        // when
        DataGroupCountWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.topicId()).isEqualTo(10L);
        assertThat(result.topicLabel()).isEqualTo("topicA");
        assertThat(result.count()).isEqualTo(5L);
    }

    @Test
    @DisplayName("ConnectedDataResponse → ConnectedDataWebResponse 매핑 성공")
    void toWebDtoFromConnectedDataResponseSuccess() {
        // given
        ConnectedDataResponse dto = new ConnectedDataResponse(
                2L, "dataset", "topicX", "typeY",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30),
                "thumb.png", 10, 2048L, 100, 20,
                LocalDateTime.of(2024, 2, 1, 9, 0),
                3L
        );

        // when
        ConnectedDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.title()).isEqualTo("dataset");
        assertThat(result.countConnectedProjects()).isEqualTo(3L);
    }

    @Test
    @DisplayName("RecentMinimalDataResponse → RecentMinimalDataWebResponse 매핑 성공")
    void toWebDtoFromRecentMinimalDataResponseSuccess() {
        // given
        RecentMinimalDataResponse dto = new RecentMinimalDataResponse(
                3L, "recentData", "thumb.png", LocalDateTime.of(2024, 3, 1, 12, 0)
        );

        // when
        RecentMinimalDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.title()).isEqualTo("recentData");
        assertThat(result.dataThumbnailUrl()).isEqualTo("thumb.png");
    }

    @Test
    @DisplayName("PopularDataResponse → PopularDataWebResponse 매핑 성공")
    void toWebDtoFromPopularDataResponseSuccess() {
        // given
        PopularDataResponse dto = new PopularDataResponse(
                4L, "popularData", "userB", "topicY", "sourceZ", "typeX",
                LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31),
                "desc", "thumb.png", 50, 1024L, 200, 30,
                LocalDateTime.of(2022, 5, 5, 15, 0), 7L
        );

        // when
        PopularDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(4L);
        assertThat(result.title()).isEqualTo("popularData");
        assertThat(result.username()).isEqualTo("userB");
        assertThat(result.countConnectedProjects()).isEqualTo(7L);
    }
}
