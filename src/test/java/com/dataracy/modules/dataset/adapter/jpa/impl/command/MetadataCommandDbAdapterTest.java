package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataMetadataJpaRepository;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MetadataCommandDbAdapterTest {

    @Mock
    private DataMetadataJpaRepository metadataRepo;

    @Mock
    private DataJpaRepository dataRepo;

    @InjectMocks
    private MetadataCommandDbAdapter adapter;

    @Captor
    private ArgumentCaptor<DataMetadataEntity> captor;

    @Test
    @DisplayName("저장 시 데이터가 존재하지 않으면 예외 발생 → NOT_FOUND_DATA")
    void saveMetadataShouldThrowWhenDataNotFound() {
        // given
        given(dataRepo.findById(99L))
                .willReturn(Optional.empty());

        // when & then
        DataException ex = catchThrowableOfType(
                () -> adapter.saveMetadata(99L, DataMetadata.of(1L, 5, 6, "json")),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("메타데이터가 없으면 새로 생성하여 저장한다")
    void saveMetadataShouldCreateNewWhenNotExisting() {
        // given
        DataEntity data = DataEntity.builder().id(1L).build();
        given(dataRepo.findById(1L))
                .willReturn(Optional.of(data));
        given(metadataRepo.findByDataId(1L))
                .willReturn(Optional.empty());
        given(metadataRepo.save(captor.capture()))
                .willAnswer(inv -> inv.getArgument(0));

        DataMetadata meta = DataMetadata.of(1L, 10, 20, "json");

        // when
        adapter.saveMetadata(1L, meta);

        // then
        DataMetadataEntity saved = captor.getValue();
        assertThat(saved.getRowCount()).isEqualTo(10);
        assertThat(saved.getColumnCount()).isEqualTo(20);
        assertThat(saved.getPreviewJson()).isEqualTo("json");

        then(metadataRepo).should().save(saved);
    }

    @Test
    @DisplayName("이미 존재하는 메타데이터가 있으면 업데이트 후 저장한다")
    void saveMetadataShouldUpdateExisting() {
        // given
        DataEntity data = DataEntity.builder().id(1L).build();
        DataMetadataEntity existing = DataMetadataEntity.builder()
                .rowCount(1)
                .columnCount(1)
                .previewJson("old")
                .build();

        given(dataRepo.findById(1L)).willReturn(Optional.of(data));
        given(metadataRepo.findByDataId(1L)).willReturn(Optional.of(existing));
        given(metadataRepo.save(existing)).willReturn(existing);

        DataMetadata meta = DataMetadata.of(1L, 99, 88, "new");

        // when
        adapter.saveMetadata(1L, meta);

        // then
        assertThat(existing.getRowCount()).isEqualTo(99);
        assertThat(existing.getColumnCount()).isEqualTo(88);
        assertThat(existing.getPreviewJson()).isEqualTo("new");

        then(metadataRepo).should().save(existing);
    }
}
