package com.dataracy.modules.project.application.port.elasticsearch;

public interface ProjectCommentUpdatePort {
    void increaseCommentCount(Long projectId);
    void decreaseCommentCount(Long projectId);
}
