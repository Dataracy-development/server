package com.dataracy.modules.dataset.adapter.jpa.entity;

import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class DataEntityTest {

    @Test
    void deleteShouldSetIsDeletedTrue() {
        // given
        DataEntity entity = DataEntity.builder().isDeleted(false).build();

        // when
        entity.delete();

        // then
        assertThat(entity.getIsDeleted()).isTrue();
    }

    @Test
    void restoreShouldSetIsDeletedFalse() {
        // given
        DataEntity entity = DataEntity.builder().isDeleted(true).build();

        // when
        entity.restore();

        // then
        assertThat(entity.getIsDeleted()).isFalse();
    }

    @Test
    void modifyShouldUpdateFields() {
        // given
        DataEntity entity = DataEntity.builder()
                .title("old")
                .topicId(1L)
                .dataSourceId(2L)
                .dataTypeId(3L)
                .description("desc")
                .analysisGuide("guide")
                .build();

        ModifyDataRequest request = new ModifyDataRequest(
                "new", 10L, 20L, 30L,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "newDesc", "newGuide"
        );

        // when
        entity.modify(request);

        // then
        assertThat(entity.getTitle()).isEqualTo("new");
        assertThat(entity.getTopicId()).isEqualTo(10L);
        assertThat(entity.getDescription()).isEqualTo("newDesc");
    }

    @Test
    void updateDataFileShouldThrowWhenUrlInvalid() {
        // given
        DataEntity entity = DataEntity.builder().build();

        // when
        DataException ex = catchThrowableOfType(
                () -> entity.updateDataFile(" ", 100L),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.INVALID_FILE_URL);
    }

    @Test
    void updateDataFileShouldUpdateUrlAndSize() {
        // given
        DataEntity entity = DataEntity.builder().dataFileUrl("old").sizeBytes(10L).build();

        // when
        entity.updateDataFile("newUrl", 200L);

        // then
        assertThat(entity.getDataFileUrl()).isEqualTo("newUrl");
        assertThat(entity.getSizeBytes()).isEqualTo(200L);
    }

    @Test
    void updateThumbnailFileShouldThrowWhenInvalid() {
        // given
        DataEntity entity = DataEntity.builder().build();

        // when
        DataException ex = catchThrowableOfType(
                () -> entity.updateDataThumbnailFile(""),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.INVALID_FILE_URL);
    }

    @Test
    void updateThumbnailFileShouldUpdateWhenDifferent() {
        // given
        DataEntity entity = DataEntity.builder().dataThumbnailUrl("old").build();

        // when
        entity.updateDataThumbnailFile("newThumb");

        // then
        assertThat(entity.getDataThumbnailUrl()).isEqualTo("newThumb");
    }

    @Test
    void increaseDownloadCountShouldIncrementByOne() {
        // given
        DataEntity entity = DataEntity.builder().downloadCount(0).build();

        // when
        entity.increaseDownloadCount();

        // then
        assertThat(entity.getDownloadCount()).isEqualTo(1);
    }
}
