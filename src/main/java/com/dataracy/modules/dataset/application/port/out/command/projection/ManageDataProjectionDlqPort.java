package com.dataracy.modules.dataset.application.port.out.command.projection;

public interface ManageDataProjectionDlqPort {
    void save(Long dataId, Integer deltaDownload, Boolean setDeleted, String lastError);
}
