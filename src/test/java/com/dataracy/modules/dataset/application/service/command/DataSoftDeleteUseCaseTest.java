package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSoftDeleteUseCaseTest {

    @InjectMocks
    private DataSoftDeleteService service;

    @Mock
    private SoftDeleteDataPort softDeleteDataPort;

    @Mock
    private ManageDataProjectionTaskPort manageDataProjectionTaskPort;

    @Test
    @DisplayName("데이터 삭제 성공 → SoftDeleteDataPort와 ManageDataProjectionTaskPort 호출 검증")
    void deleteDataShouldInvokePorts() {
        // when
        service.deleteData(1L);

        // then
        then(softDeleteDataPort).should().deleteData(1L);
        then(manageDataProjectionTaskPort).should().enqueueSetDeleted(1L, true);
    }

    @Test
    @DisplayName("데이터 복원 성공 → SoftDeleteDataPort와 ManageDataProjectionTaskPort 호출 검증")
    void restoreDataShouldInvokePorts() {
        // when
        service.restoreData(2L);

        // then
        then(softDeleteDataPort).should().restoreData(2L);
        then(manageDataProjectionTaskPort).should().enqueueSetDeleted(2L, false);
    }
}
