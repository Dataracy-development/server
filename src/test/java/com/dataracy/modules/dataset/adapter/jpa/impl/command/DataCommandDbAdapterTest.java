package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
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
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataCommandDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private DataCommandDbAdapter adapter;

    @Nested
    @DisplayName("데이터셋 업로드")
    class UploadData {

        @Test
        @DisplayName("데이터 저장 성공 → Domain 객체 반환")
        void saveDataShouldPersistAndReturnDomain() {
            // given
            Data data = Data.of(
                    1L,
                    "title",
                    1L,
                    1L,
                    1L,
                    1L,
                    null,
                    null,
                    "desc",
                    "guide",
                    null,
                    null,
                    0,
                    null,
                    null,
                    null
            );
            DataEntity entity = DataEntityMapper.toEntity(data);
            given(repo.save(any(DataEntity.class)))
                    .willReturn(entity);

            // when
            Data saved = adapter.saveData(data);

            // then
            assertThat(saved.getTitle()).isEqualTo("title");
        }
    }

    @Nested
    @DisplayName("데이터셋 파일 업데이트")
    class UpdateDataFile {

        @Test
        @DisplayName("updateDataFile 성공 → URL 업데이트 확인")
        void updateDataFileShouldUpdateWhenFound() {
            // given
            DataEntity entity = DataEntity.builder().dataFileUrl("old").build();
            given(repo.findById(1L))
                    .willReturn(Optional.of(entity));

            // when
            adapter.updateDataFile(1L, "newUrl", 100L);

            // then
            assertThat(entity.getDataFileUrl()).isEqualTo("newUrl");
        }

        @Test
        @DisplayName("updateDataFile 실패 → 데이터 없음 시 NOT_FOUND_DATA 예외")
        void updateDataFileShouldThrowWhenNotFound() {
            // given
            given(repo.findById(99L))
                    .willReturn(Optional.empty());

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> adapter.updateDataFile(99L, "url", 10L),
                    DataException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }
    }

    @Nested
    @DisplayName("데이터셋 썸네일 파일 업데이트")
    class UpdateThumbnailFile {

        @Test
        @DisplayName("updateThumbnailFile 성공 → 썸네일 업데이트 확인")
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
        @DisplayName("updateThumbnailFile 실패 → 데이터 없음 시 NOT_FOUND_DATA 예외")
        void updateThumbnailFileShouldThrowWhenNotFound() {
            // given
            given(repo.findById(99L)).willReturn(Optional.empty());

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> adapter.updateThumbnailFile(99L, "thumb"),
                    DataException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }
    }

    @Nested
    @DisplayName("데이터셋 내용 수정")
    class ModifyData {

        @Test
        @DisplayName("modifyData 성공 → title, description 변경 확인")
        void modifyDataShouldUpdateWhenFound() {
            // given
            DataEntity entity = DataEntity.builder().title("old").description("old").build();
            given(repo.findById(1L)).willReturn(Optional.of(entity));
            ModifyDataRequest req = new ModifyDataRequest(
                    "new",
                    1L,
                    1L,
                    1L,
                    null,
                    null,
                    "desc",
                    "guide"
            );

            // when
            adapter.modifyData(1L, req);

            // then
            assertThat(entity.getTitle()).isEqualTo("new");
            assertThat(entity.getDescription()).isEqualTo("desc");
        }

        @Test
        @DisplayName("modifyData 실패 → 데이터 없음 시 NOT_FOUND_DATA 예외")
        void modifyDataShouldThrowWhenNotFound() {
            // given
            given(repo.findById(99L)).willReturn(Optional.empty());
            ModifyDataRequest request = new ModifyDataRequest(
                    "t",
                    1L,
                    1L,
                    1L,
                    LocalDate.now(),
                    LocalDate.now(),
                    "d",
                    "g"
            );

            // when & then
            DataException ex = catchThrowableOfType(
                    () -> adapter.modifyData(99L, request),
                    DataException.class
            );
            assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
        }
    }
}
