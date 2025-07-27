package com.dataracy.modules.project.application.port.out;

import java.util.Set;

public interface ProjectViewCountRedisPort {
    void increaseViewCount(Long projectId, String viewerId, String targetType);
    Long getViewCount(Long projectId, String targetType);
    Set<String> getAllViewCountKeys(String targetType);
    void clearViewCount(Long targetId, String targetType);
}
