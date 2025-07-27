package com.dataracy.modules.project.adapter.scheduler;

import com.dataracy.modules.project.application.port.elasticsearch.ProjectViewUpdatePort;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.application.port.out.ProjectViewCountRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProjectViewCountScheduler {

    private final ProjectViewCountRedisPort projectViewCountRedisPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectViewUpdatePort projectViewUpdatePort;

    @Scheduled(fixedRate = 60 * 1000) // 1분
    @Transactional
    public void flushProjectViews() {
        Set<String> keys = projectViewCountRedisPort.getAllViewCountKeys("PROJECT");

        for (String key : keys) {
            Long projectId = extractProjectId(key);
            Long count = projectViewCountRedisPort.getViewCount(projectId, "PROJECT");

            if (count > 0) {
                projectRepositoryPort.increaseViewCount(projectId, count);
                projectViewUpdatePort.increaseViewCount(projectId, count);
                projectViewCountRedisPort.clearViewCount(projectId, "PROJECT");
            }
        }
    }

    private Long extractProjectId(String key) {
        // ex. viewCount:PROJECT:123
        return Long.parseLong(key.split(":")[2]);
    }
}

