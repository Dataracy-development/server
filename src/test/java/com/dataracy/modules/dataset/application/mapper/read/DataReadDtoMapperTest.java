package com.dataracy.modules.dataset.application.mapper.read;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;
import com.dataracy.modules.dataset.application.dto.response.read.DataDetailResponse;
import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataReadDtoMapperTest {

    private final DataReadDtoMapper mapper = new DataReadDtoMapper();

    private Data sampleData() {
        return Data.of(
                1L, "title", 2L, 3L, 4L, 5L,
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "desc", "guide", "fileUrl", "thumbUrl",
                10, 100L,
                DataMetadata.of(1L, 100, 10, "{}"),
                LocalDateTime.now()
        );
    }

    @Test
    void toConnectedDataResponseShouldMapCorrectly() {
        ConnectedDataResponse res = mapper.toResponseDto(sampleData(), "topic", "type", 5L);
        assertThat(res.title()).isEqualTo("title");
        assertThat(res.countConnectedProjects()).isEqualTo(5L);
    }

    @Test
    void toRecentMinimalDataResponseShouldMapCorrectly() {
        RecentMinimalDataResponse res = mapper.toResponseDto(sampleData());
        assertThat(res.title()).isEqualTo("title");
    }

    @Test
    void toPopularDataResponseShouldMapCorrectly() {
        PopularDataResponse res = mapper.toResponseDto(sampleData(), "user", "topic", "src", "type", 7L);
        assertThat(res.username()).isEqualTo("user");
        assertThat(res.countConnectedProjects()).isEqualTo(7L);
    }

    @Test
    void toDataDetailResponseShouldMapCorrectly() {
        DataDetailResponse res = mapper.toResponseDto(sampleData(), "nick", "profile", "intro", "author", "occ", "topic", "src", "type");
        assertThat(res.username()).isEqualTo("nick");
        assertThat(res.occupationLabel()).isEqualTo("occ");
    }
}
