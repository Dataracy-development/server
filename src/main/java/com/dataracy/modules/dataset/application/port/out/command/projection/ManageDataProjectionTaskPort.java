package com.dataracy.modules.dataset.application.port.out.command.projection;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;

public interface ManageDataProjectionTaskPort {
    void enqueueSetDeleted(Long dataId, boolean deleted);
    void enqueueDownloadDelta(Long dataId, int deltaDownload);
    void delete(DataEsProjectionTaskEntity dataEsProjectionTask);
}
