package com.dataracy.modules.dataset.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataTest {

    @Test
    @DisplayName("of() 정적 팩토리 메서드로 Data 객체가 올바르게 생성된다")
    void ofShouldBuildDataCorrectly() {
        // given
        LocalDateTime now = LocalDateTime.now();
        DataMetadata metadata = DataMetadata.of(
                1L,
                100,
                10,
                "{}"
        );

        // when
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
                12345L,
                metadata,
                now
        );

        // then
        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getTitle()).isEqualTo("title");
        assertThat(data.getTopicId()).isEqualTo(2L);
        assertThat(data.getUserId()).isEqualTo(3L);
        assertThat(data.getDataSourceId()).isEqualTo(4L);
        assertThat(data.getDataTypeId()).isEqualTo(5L);
        assertThat(data.getDownloadCount()).isEqualTo(10);
        assertThat(data.getSizeBytes()).isEqualTo(12345L);
        assertThat(data.getMetadata()).isEqualTo(metadata);
        assertThat(data.getCreatedAt()).isEqualTo(now);
    }
}
