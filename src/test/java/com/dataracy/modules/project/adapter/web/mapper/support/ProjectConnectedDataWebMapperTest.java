package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ProjectConnectedDataWebMapperTest {

    private final ProjectConnectedDataWebMapper mapper = new ProjectConnectedDataWebMapper();

    @Test
    @DisplayName("성공 → ProjectConnectedDataResponse를 ProjectConnectedDataWebResponse로 매핑")
    void toWebDtoSuccess() {
        // given
        LocalDate startDate = LocalDate.of(2025, 8, 1);
        LocalDate endDate = LocalDate.of(2025, 8, 5);
        LocalDateTime createdAt = LocalDateTime.of(2025, 8, 4, 10, 30);

        ProjectConnectedDataResponse responseDto = new ProjectConnectedDataResponse(
                1L, "dataset-title", 1L, "userA", "https://~~", "topic-label", "CSV",
                startDate, endDate,
                "thumb.png", 3, 55, 100,
                createdAt, 5L
        );

        // when
        ProjectConnectedDataWebResponse webResponse = mapper.toWebDto(responseDto);

        // then
        assertThat(webResponse.id()).isEqualTo(1L);
        assertThat(webResponse.title()).isEqualTo("dataset-title");
        assertThat(webResponse.topicLabel()).isEqualTo("topic-label");
        assertThat(webResponse.dataTypeLabel()).isEqualTo("CSV");
        assertThat(webResponse.startDate()).isEqualTo(startDate);
        assertThat(webResponse.endDate()).isEqualTo(endDate);
        assertThat(webResponse.dataThumbnailUrl()).isEqualTo("thumb.png");
        assertThat(webResponse.downloadCount()).isEqualTo(3);
        assertThat(webResponse.rowCount()).isEqualTo(55);
        assertThat(webResponse.columnCount()).isEqualTo(100);
        assertThat(webResponse.createdAt()).isEqualTo(createdAt);
        assertThat(webResponse.countConnectedProjects()).isEqualTo(5L);
    }

    @Test
    @DisplayName("예외 발생 → 입력이 null이면 NullPointerException")
    void toWebDtoNullInputThrowsException() {
        // given
        ProjectConnectedDataResponse responseDto = null;

        // when
        Throwable thrown = catchThrowable(() -> mapper.toWebDto(responseDto));

        // then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }
}

