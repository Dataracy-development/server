package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataSearchWebMapperTest {

    private final DataSearchWebMapper mapper = new DataSearchWebMapper();

    @Test
    @DisplayName("SimilarDataResponse → SimilarDataWebResponse 매핑 성공")
    void toWebDtoFromSimilarDataResponseSuccess() {
        // given
        SimilarDataResponse dto = new SimilarDataResponse(
                1L, "title", 1L, "userA", "https://~~", "topic", "source", "type",
                LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31),
                "desc", "thumb.png", 10, 2048L, 100, 20,
                LocalDateTime.of(2022, 6, 1, 15, 0)
        );

        // when
        SimilarDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.creatorId()).isEqualTo(1L);
        assertThat(result.creatorName()).isEqualTo("userA");
        assertThat(result.downloadCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("RecentMinimalDataResponse → RecentMinimalDataWebResponse 매핑 성공")
    void toWebDtoFromRecentMinimalDataResponseSuccess() {
        // given
        RecentMinimalDataResponse dto = new RecentMinimalDataResponse(
                2L, "recentData", 1L, "userA", "https://~~", "thumb.png", LocalDateTime.of(2023, 7, 1, 12, 0)
        );

        // when
        RecentMinimalDataWebResponse result = mapper.toWebDto(dto);

        // then
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.title()).isEqualTo("recentData");
        assertThat(result.creatorId()).isEqualTo(1L);
        assertThat(result.creatorName()).isEqualTo("userA");
        assertThat(result.dataThumbnailUrl()).isEqualTo("thumb.png");
    }
}
