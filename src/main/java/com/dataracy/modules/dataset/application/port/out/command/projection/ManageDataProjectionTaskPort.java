package com.dataracy.modules.dataset.application.port.out.command.projection;

public interface ManageDataProjectionTaskPort {
    void enqueueSetDeleted(Long dataId, boolean deleted);
    void enqueueDownloadDelta(Long dataId, int deltaDownload);
    void delete(Long dataEsProjectionTaskId);
}
