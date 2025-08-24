package com.dataracy.modules.dataset.adapter.jpa.impl.command;

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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SoftDeleteDataDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private SoftDeleteDataDbAdapter adapter;

    @Test
    void deleteDataShouldThrowWhenNotFound() {
        // given
        given(repo.findById(99L)).willReturn(Optional.empty());

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.deleteData(99L),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void deleteDataShouldSetDeleted() {
        // given
        DataEntity entity = DataEntity.builder().isDeleted(false).build();
        given(repo.findById(1L)).willReturn(Optional.of(entity));

        // when
        adapter.deleteData(1L);

        // then
        assertThat(entity.getIsDeleted()).isTrue();
        then(repo).should().save(entity);
    }

    @Test
    void restoreDataShouldThrowWhenNotFound() {
        // given
        given(repo.findIncludingDeletedData(99L)).willReturn(Optional.empty());

        // when
        DataException ex = catchThrowableOfType(
                () -> adapter.restoreData(99L),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void restoreDataShouldUnsetDeleted() {
        // given
        DataEntity entity = DataEntity.builder().isDeleted(true).build();
        given(repo.findIncludingDeletedData(1L)).willReturn(Optional.of(entity));

        // when
        adapter.restoreData(1L);

        // then
        assertThat(entity.getIsDeleted()).isFalse();
        then(repo).should().save(entity);
    }
}
