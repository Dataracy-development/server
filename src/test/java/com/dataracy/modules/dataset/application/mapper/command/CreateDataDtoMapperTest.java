package com.dataracy.modules.dataset.application.mapper.command;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.domain.model.Data;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateDataDtoMapperTest {

    @Test
    void toDomainShouldMapCorrectly() {
        CreateDataDtoMapper mapper = new CreateDataDtoMapper();
        ReflectionTestUtils.setField(mapper, "defaultDatasetImageUrl", "default.png");

        UploadDataRequest req = new UploadDataRequest(
                "title", 2L, 3L, 4L,
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "desc", "guide"
        );

        Data data = mapper.toDomain(req, 99L);

        assertThat(data.getTitle()).isEqualTo("title");
        assertThat(data.getUserId()).isEqualTo(99L);
        assertThat(data.getDataThumbnailUrl()).isEqualTo("default.png");
    }
}
