package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataSoftDeleteUseCaseTest {

    @InjectMocks
    private DataSoftDeleteUseCase service;

    @Mock private SoftDeleteDataPort softDeleteDataPort;
    @Mock private ManageDataProjectionTaskPort manageDataProjectionTaskPort;

    @Test
    void deleteDataShouldInvokePorts() {
        service.deleteData(1L);
        then(softDeleteDataPort).should().deleteData(1L);
        then(manageDataProjectionTaskPort).should().enqueueSetDeleted(1L, true);
    }

    @Test
    void restoreDataShouldInvokePorts() {
        service.restoreData(2L);
        then(softDeleteDataPort).should().restoreData(2L);
        then(manageDataProjectionTaskPort).should().enqueueSetDeleted(2L, false);
    }
}
