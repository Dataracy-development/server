package com.dataracy.modules.dataset.adapter.web.mapper.command;

import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.command.UploadDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DataCommandWebMapperTest {

    private final DataCommandWebMapper mapper = new DataCommandWebMapper();

    @Test
    @DisplayName("UploadDataWebRequest → UploadDataRequest 매핑 성공")
    void toApplicationDtoFromUploadWebRequestSuccess() {
        // given
        UploadDataWebRequest webRequest = new UploadDataWebRequest(
                "title",
                1L,
                2L,
                3L,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "desc",
                "guide"
        );

        // when
        UploadDataRequest result = mapper.toApplicationDto(webRequest);

        // then
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.topicId()).isEqualTo(1L);
        assertThat(result.dataSourceId()).isEqualTo(2L);
        assertThat(result.dataTypeId()).isEqualTo(3L);
        assertThat(result.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(result.description()).isEqualTo("desc");
        assertThat(result.analysisGuide()).isEqualTo("guide");
    }

    @Test
    @DisplayName("UploadDataResponse → UploadDataWebResponse 매핑 성공")
    void toWebDtoFromUploadResponseSuccess() {
        // given
        UploadDataResponse response = new UploadDataResponse(100L);

        // when
        UploadDataWebResponse result = mapper.toWebDto(response);

        // then
        assertThat(result.id()).isEqualTo(100L);
    }

    @Test
    @DisplayName("ModifyDataWebRequest → ModifyDataRequest 매핑 성공")
    void toApplicationDtoFromModifyWebRequestSuccess() {
        // given
        ModifyDataWebRequest webRequest = new ModifyDataWebRequest(
                "modTitle",
                10L,
                20L,
                30L,
                LocalDate.of(2023, 5, 1),
                LocalDate.of(2023, 10, 1),
                "modDesc",
                "modGuide"
        );

        // when
        ModifyDataRequest result = mapper.toApplicationDto(webRequest);

        // then
        assertThat(result.title()).isEqualTo("modTitle");
        assertThat(result.topicId()).isEqualTo(10L);
        assertThat(result.dataSourceId()).isEqualTo(20L);
        assertThat(result.dataTypeId()).isEqualTo(30L);
        assertThat(result.startDate()).isEqualTo(LocalDate.of(2023, 5, 1));
        assertThat(result.endDate()).isEqualTo(LocalDate.of(2023, 10, 1));
        assertThat(result.description()).isEqualTo("modDesc");
        assertThat(result.analysisGuide()).isEqualTo("modGuide");
    }
}
