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

    /**
     * Redis에 저장된 프로젝트 조회수를 주기적으로 메인 프로젝트 저장소와 프로젝트 뷰 표현에 동기화합니다.
     *
     * 1분마다 실행되며, Redis에서 프로젝트별 조회수 키를 모두 조회한 후 각 프로젝트의 조회수를 가져옵니다.
     * 조회수가 0보다 크면, 해당 값을 프로젝트 저장소와 뷰 표현에 반영하고 Redis의 조회수는 초기화합니다.
     */
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

    /**
     * Redis 키 문자열에서 프로젝트 ID를 추출하여 반환합니다.
     *
     * @param key "viewCount:PROJECT:<projectId>" 형식의 Redis 키 문자열
     * @return 추출된 프로젝트 ID
     */
    private Long extractProjectId(String key) {
        // ex. viewCount:PROJECT:123
        return Long.parseLong(key.split(":")[2]);
    }
}

