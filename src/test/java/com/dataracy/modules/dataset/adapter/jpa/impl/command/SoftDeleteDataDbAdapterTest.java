package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class SoftDeleteDataDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private SoftDeleteDataDbAdapter adapter;

    @Nested
    @DisplayName("데이터셋 삭제")
    class SoftDeleteData {

        @Test
        @DisplayName("삭제 시 데이터가 없으면 예외 발생 → NOT_FOUND_DATA")
        void deleteDataShouldThrowWhenNotFound() {
            // given
            given(repo.findById(99L))
                    .willReturn(Optional.empty());

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> adapter.deleteData(99L),
                    DataException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }

        @Test
        @DisplayName("삭제 시 데이터의 isDeleted 플래그를 true로 변경한다")
        void deleteDataShouldSetDeleted() {
            // given
            DataEntity entity = DataEntity.builder().isDeleted(false).build();
            given(repo.findById(1L))
                    .willReturn(Optional.of(entity));

            // when
            adapter.deleteData(1L);

            // then
            assertThat(entity.getIsDeleted()).isTrue();
            then(repo).should().save(entity);
        }

    }

    @Nested
    @DisplayName("데이터셋 복원")
    class RestoreData {

        @Test
        @DisplayName("복원 시 데이터가 없으면 예외 발생 → NOT_FOUND_DATA")
        void restoreDataShouldThrowWhenNotFound() {
            // given
            given(repo.findIncludingDeletedData(99L))
                    .willReturn(Optional.empty());

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> adapter.restoreData(99L),
                    DataException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }

        @Test
        @DisplayName("복원 시 데이터의 isDeleted 플래그를 false로 변경한다")
        void restoreDataShouldUnsetDeleted() {
            // given
            DataEntity entity = DataEntity.builder().isDeleted(true).build();
            given(repo.findIncludingDeletedData(1L))
                    .willReturn(Optional.of(entity));

            // when
            adapter.restoreData(1L);

            // then
            assertThat(entity.getIsDeleted()).isFalse();
            then(repo).should().save(entity);
        }
    }
}
