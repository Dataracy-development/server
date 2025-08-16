package com.dataracy.modules.project.application.port.out.command.projection;

public interface EnqueueProjectProjectionPort {
    void enqueueCommentDelta(Long projectId, int deltaComment);
    void enqueueLikeDelta(Long projectId, int deltaLike);
    void enqueueSetDeleted(Long projectId, boolean deleted);
}
