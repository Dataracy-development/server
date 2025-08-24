package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DataCommandDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private DataCommandDbAdapter adapter;

    @Test
    void saveDataShouldPersistAndReturnDomain() {
        // given
        Data data = Data.of(1L, "title", 1L, 1L, 1L, 1L,
                null, null, "desc", "guide",
                null, null, 0, null, null, null);
        DataEntity entity = DataEntityMapper.toEntity(data);
        given(repo.save(any(DataEntity.class))).willReturn(entity);

        // when
        Data saved = adapter.saveData(data);

        // then
        assertThat(saved.getTitle()).isEqualTo("title");
    }


    @Test
    void updateDataFileShouldThrowWhenNotFound() {
        // given
        given(repo.findById(99L)).willReturn(Optional.empty());

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.updateDataFile(99L, "url", 10L),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void updateDataFileShouldUpdateWhenFound() {
        // given
        DataEntity entity = DataEntity.builder().dataFileUrl("old").build();
        given(repo.findById(1L)).willReturn(Optional.of(entity));

        // when
        adapter.updateDataFile(1L, "newUrl", 100L);

        // then
        assertThat(entity.getDataFileUrl()).isEqualTo("newUrl");
    }

    @Test
    void updateThumbnailFileShouldThrowWhenNotFound() {
        // given
        given(repo.findById(99L)).willReturn(Optional.empty());

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.updateThumbnailFile(99L, "thumb"),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void updateThumbnailFileShouldUpdateWhenFound() {
        // given
        DataEntity entity = DataEntity.builder().dataThumbnailUrl("old").build();
        given(repo.findById(1L)).willReturn(Optional.of(entity));

        // when
        adapter.updateThumbnailFile(1L, "newThumb");

        // then
        assertThat(entity.getDataThumbnailUrl()).isEqualTo("newThumb");
    }

    @Test
    void modifyDataShouldThrowWhenNotFound() {
        // given
        given(repo.findById(99L)).willReturn(Optional.empty());
        ModifyDataRequest request = new ModifyDataRequest(
                "t", 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), "d", "g"
        );

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.modifyData(99L, request),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void modifyDataShouldUpdateWhenFound() {
        // given
        DataEntity entity = DataEntity.builder().title("old").description("old").build();
        given(repo.findById(1L)).willReturn(Optional.of(entity));
        ModifyDataRequest req = new ModifyDataRequest(
                "new", 1L, 1L, 1L, null, null, "desc", "guide"
        );

        // when
        adapter.modifyData(1L, req);

        // then
        assertThat(entity.getTitle()).isEqualTo("new");
        assertThat(entity.getDescription()).isEqualTo("desc");
    }
}
