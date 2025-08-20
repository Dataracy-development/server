package com.dataracy.modules.project.application.port.out.command.projection;

public interface ManageProjectProjectionDlqPort {
    void save(Long projectId, Integer deltaComment, Integer deltaLike, Long deltaView, Boolean setDeleted, String lastError);
}
