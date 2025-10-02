package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSoftDeleteServiceTest {

    @InjectMocks
    private DataSoftDeleteService service;

    @Mock
    private SoftDeleteDataPort softDeleteDataDbPort;

    @Mock
    private ManageDataProjectionTaskPort manageDataProjectionTaskPort;

    @Nested
    @DisplayName("데이터 소프트 삭제")
    class DeleteData {

        @Test
        @DisplayName("데이터 소프트 삭제 성공")
        void deleteDataSuccess() {
            // given
            Long dataId = 1L;
            willDoNothing().given(softDeleteDataDbPort).deleteData(dataId);
            willDoNothing().given(manageDataProjectionTaskPort).enqueueSetDeleted(dataId, true);

            // when
            service.deleteData(dataId);

            // then
            then(softDeleteDataDbPort).should().deleteData(dataId);
            then(manageDataProjectionTaskPort).should().enqueueSetDeleted(dataId, true);
        }

        @Test
        @DisplayName("데이터 소프트 삭제 실패 - DB 삭제 실패")
        void deleteDataFail_DbDeleteFailure() {
            // given
            Long dataId = 1L;
            willThrow(new RuntimeException("DB 삭제 실패"))
                    .given(softDeleteDataDbPort).deleteData(dataId);

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                service.deleteData(dataId);
            });

            then(softDeleteDataDbPort).should().deleteData(dataId);
            then(manageDataProjectionTaskPort).should(never()).enqueueSetDeleted(any(), anyBoolean());
        }

        @Test
        @DisplayName("데이터 소프트 삭제 실패 - 프로젝션 작업 등록 실패")
        void deleteDataFail_ProjectionTaskFailure() {
            // given
            Long dataId = 1L;
            willDoNothing().given(softDeleteDataDbPort).deleteData(dataId);
            willThrow(new RuntimeException("프로젝션 작업 등록 실패"))
                    .given(manageDataProjectionTaskPort).enqueueSetDeleted(dataId, true);

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                service.deleteData(dataId);
            });

            then(softDeleteDataDbPort).should().deleteData(dataId);
            then(manageDataProjectionTaskPort).should().enqueueSetDeleted(dataId, true);
        }
    }

    @Nested
    @DisplayName("데이터 복원")
    class RestoreData {

        @Test
        @DisplayName("데이터 복원 성공")
        void restoreDataSuccess() {
            // given
            Long dataId = 1L;
            willDoNothing().given(softDeleteDataDbPort).restoreData(dataId);
            willDoNothing().given(manageDataProjectionTaskPort).enqueueSetDeleted(dataId, false);

            // when
            service.restoreData(dataId);

            // then
            then(softDeleteDataDbPort).should().restoreData(dataId);
            then(manageDataProjectionTaskPort).should().enqueueSetDeleted(dataId, false);
        }

        @Test
        @DisplayName("데이터 복원 실패 - DB 복원 실패")
        void restoreDataFail_DbRestoreFailure() {
            // given
            Long dataId = 1L;
            willThrow(new RuntimeException("DB 복원 실패"))
                    .given(softDeleteDataDbPort).restoreData(dataId);

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                service.restoreData(dataId);
            });

            then(softDeleteDataDbPort).should().restoreData(dataId);
            then(manageDataProjectionTaskPort).should(never()).enqueueSetDeleted(any(), anyBoolean());
        }

        @Test
        @DisplayName("데이터 복원 실패 - 프로젝션 작업 등록 실패")
        void restoreDataFail_ProjectionTaskFailure() {
            // given
            Long dataId = 1L;
            willDoNothing().given(softDeleteDataDbPort).restoreData(dataId);
            willThrow(new RuntimeException("프로젝션 작업 등록 실패"))
                    .given(manageDataProjectionTaskPort).enqueueSetDeleted(dataId, false);

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                service.restoreData(dataId);
            });

            then(softDeleteDataDbPort).should().restoreData(dataId);
            then(manageDataProjectionTaskPort).should().enqueueSetDeleted(dataId, false);
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTests {

        @Test
        @DisplayName("null 데이터 ID로 삭제 시도")
        void deleteDataWithNullId() {
            // given
            willDoNothing().given(softDeleteDataDbPort).deleteData(null);
            willDoNothing().given(manageDataProjectionTaskPort).enqueueSetDeleted(null, true);

            // when
            service.deleteData(null);

            // then
            then(softDeleteDataDbPort).should().deleteData(null);
            then(manageDataProjectionTaskPort).should().enqueueSetDeleted(null, true);
        }

        @Test
        @DisplayName("음수 데이터 ID로 복원 시도")
        void restoreDataWithNegativeId() {
            // given
            Long negativeId = -1L;
            willDoNothing().given(softDeleteDataDbPort).restoreData(negativeId);
            willDoNothing().given(manageDataProjectionTaskPort).enqueueSetDeleted(negativeId, false);

            // when
            service.restoreData(negativeId);

            // then
            then(softDeleteDataDbPort).should().restoreData(negativeId);
            then(manageDataProjectionTaskPort).should().enqueueSetDeleted(negativeId, false);
        }
    }
}
