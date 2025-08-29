package com.dataracy.modules.dataset.application.mapper.read;

import com.dataracy.modules.dataset.application.dto.response.read.*;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataReadDtoMapperTest {

    private final DataReadDtoMapper mapper = new DataReadDtoMapper();

    private Data sampleData() {
        return Data.of(
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
                100L,
                DataMetadata.of(1L, 100, 10, "{}"),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("toConnectedDataResponse - Data → ConnectedDataResponse 매핑 성공")
    void toConnectedDataResponseShouldMapCorrectly() {
        // given
        Data data = sampleData();

        // when
        ConnectedDataResponse res = mapper.toResponseDto(data, "userA", "profile.png", "topic", "type", 5L);

        // then
        assertThat(res.title()).isEqualTo("title");
        assertThat(res.countConnectedProjects()).isEqualTo(5L);
    }

    @Test
    @DisplayName("toRecentMinimalDataResponse - Data → RecentMinimalDataResponse 매핑 성공")
    void toRecentMinimalDataResponseShouldMapCorrectly() {
        // given
        Data data = sampleData();

        // when
        RecentMinimalDataResponse res = mapper.toResponseDto(data, "userA", "profile.png");

        // then
        assertThat(res.title()).isEqualTo("title");
    }

    @Test
    @DisplayName("toPopularDataResponse - Data → PopularDataResponse 매핑 성공")
    void toPopularDataResponseShouldMapCorrectly() {
        // given
        Data data = sampleData();

        // when
        PopularDataResponse res = mapper.toResponseDto(data, "user", "profile.png", "topic", "src", "type", 7L);

        // then
        assertThat(res.creatorName()).isEqualTo("user");
        assertThat(res.countConnectedProjects()).isEqualTo(7L);
    }

    @Test
    @DisplayName("toDataDetailResponse - Data → DataDetailResponse 매핑 성공")
    void toDataDetailResponseShouldMapCorrectly() {
        // given
        Data data = sampleData();

        // when
        DataDetailResponse res = mapper.toResponseDto(
                data,
                "nick",
                "profile",
                "intro",
                "author",
                "occ",
                "topic",
                "src",
                "type"
        );

        // then
        assertThat(res.creatorName()).isEqualTo("nick");
        assertThat(res.occupationLabel()).isEqualTo("occ");
    }

    @Test
    @DisplayName("toUserDataResponse - Data → UserDataResponse 매핑 성공")
    void toUserDataResponseResponseShouldMapCorrectly() {
        // given
        Data data = sampleData();

        // when
        UserDataResponse res = mapper.toResponseDto(
                data,
                "topic",
                "type",
                3L
        );

        // then
        assertThat(res.title()).isEqualTo("title");
        assertThat(res.topicLabel()).isEqualTo("topic");
        assertThat(res.dataTypeLabel()).isEqualTo("type");
        assertThat(res.countConnectedProjects()).isEqualTo(3L);
    }
}
