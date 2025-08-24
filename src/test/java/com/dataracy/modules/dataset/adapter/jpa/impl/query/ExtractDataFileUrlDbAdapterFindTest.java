package com.dataracy.modules.dataset.adapter.jpa.impl.query;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtractDataFileUrlDbAdapterFindTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private ExtractDataFileUrlDbAdapterFind adapter;

    @Test
    void findUserIdByDataIdShouldThrowWhenNotFound() {
        // given
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.findUserIdByDataId(99L),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void findUserIdByDataIdShouldReturnUserId() {
        // given
        DataEntity entity = DataEntity.builder().userId(10L).build();
        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        // when
        Long userId = adapter.findUserIdByDataId(1L);

        // then
        assertThat(userId).isEqualTo(10L);
    }

    @Test
    void findUserIdIncludingDeletedShouldThrowWhenNotFound() {
        // given
        when(repo.findIncludingDeletedData(99L)).thenReturn(Optional.empty());

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.findUserIdIncludingDeleted(99L),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void findUserIdIncludingDeletedShouldReturnUserId() {
        // given
        DataEntity entity = DataEntity.builder().userId(20L).build();
        when(repo.findIncludingDeletedData(1L)).thenReturn(Optional.of(entity));

        // when
        Long userId = adapter.findUserIdIncludingDeleted(1L);

        // then
        assertThat(userId).isEqualTo(20L);
    }

    @Test
    void findDownloadedDataFileUrlShouldReturnEmptyWhenNotPresent() {
        // given
        when(repo.findDataFileUrlById(1L)).thenReturn(Optional.empty());

        // when
        Optional<String> result = adapter.findDownloadedDataFileUrl(1L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findDownloadedDataFileUrlShouldReturnUrlWhenPresent() {
        // given
        when(repo.findDataFileUrlById(1L)).thenReturn(Optional.of("fileUrl"));

        // when
        Optional<String> result = adapter.findDownloadedDataFileUrl(1L);

        // then
        assertThat(result).contains("fileUrl");
    }
}
