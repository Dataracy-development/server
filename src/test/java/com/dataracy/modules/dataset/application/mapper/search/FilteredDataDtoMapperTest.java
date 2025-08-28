package com.dataracy.modules.dataset.application.mapper.search;

import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FilteredDataDtoMapperTest {

    private final FilteredDataDtoMapper mapper = new FilteredDataDtoMapper();

    @Test
    @DisplayName("toResponseDto - Data → FilteredDataResponse 매핑 성공")
    void toResponseDtoShouldMapCorrectly() {
        // given
        Data data = Data.of(
                1L,
                "title",
                2L,
                3L,
                4L,
                5L,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "desc",
                "guide",
                "fileUrl",
                "thumbUrl",
                10,
                123L,
                DataMetadata.of(1L, 100, 10, "{}"),
                LocalDateTime.now()
        );

        // when
        FilteredDataResponse res = mapper.toResponseDto(data, "userA", "profile.png", "topic", "src", "type", 9L);

        // then
        assertThat(res.title()).isEqualTo("title");
        assertThat(res.countConnectedProjects()).isEqualTo(9L);
    }
}
