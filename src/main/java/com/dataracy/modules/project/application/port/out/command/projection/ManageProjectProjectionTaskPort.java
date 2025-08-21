package com.dataracy.modules.project.application.port.out.command.projection;

public interface ManageProjectProjectionTaskPort {
    void enqueueCommentDelta(Long projectId, int deltaComment);
    void enqueueLikeDelta(Long projectId, int deltaLike);
    void enqueueViewDelta(Long projectId, Long deltaView);
    void enqueueSetDeleted(Long projectId, boolean deleted);
    void delete(Long projectEsProjectionTaskId);
}
