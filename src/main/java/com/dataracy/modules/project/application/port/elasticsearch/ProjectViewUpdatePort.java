package com.dataracy.modules.project.application.port.elasticsearch;

public interface ProjectViewUpdatePort {
    void increaseViewCount(Long projectId, Long increment);
}
