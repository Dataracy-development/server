package com.dataracy.modules.dataset.application.mapper.command;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.domain.model.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CreateDataDtoMapperTest {

    @Test
    @DisplayName("UploadDataRequest → Data 도메인 매핑 성공")
    void toDomainShouldMapCorrectly() {
        // given
        CreateDataDtoMapper mapper = new CreateDataDtoMapper();
        ReflectionTestUtils.setField(mapper, "defaultDatasetImageUrl", "default.png");

        UploadDataRequest req = new UploadDataRequest(
                "title",
                2L,
                3L,
                4L,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "desc",
                "guide"
        );

        // when
        Data data = mapper.toDomain(req, 99L);

        // then
        assertAll(
                () -> assertThat(data.getTitle()).isEqualTo("title"),
                () -> assertThat(data.getUserId()).isEqualTo(99L),
                () -> assertThat(data.getDataThumbnailUrl()).isEqualTo("default.png")
        );
    }
}
