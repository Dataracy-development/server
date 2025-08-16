package com.dataracy.modules.dataset.application.port.out.command.projection;

public interface EnqueueDataProjectionPort {
    void enqueueSetDeleted(Long dataId, boolean deleted);
    void enqueueDownloadDelta(Long dataId, int deltaDownload);
}
