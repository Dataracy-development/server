package com.dataracy.modules.project.adapter.scheduler;

import com.dataracy.modules.project.application.port.elasticsearch.ProjectViewUpdatePort;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.application.port.out.ProjectViewCountRedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectViewCountScheduler {

    private final ProjectViewCountRedisPort projectViewCountRedisPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectViewUpdatePort projectViewUpdatePort;

    /**
     * Redis에 저장된 프로젝트별 조회수를 1분마다 메인 프로젝트 저장소와 프로젝트 뷰에 동기화합니다.
     *
     * 각 프로젝트의 조회수를 Redis에서 가져와 0보다 크면 저장소와 뷰에 반영한 뒤, 해당 Redis 조회수를 초기화합니다.
     * 개별 키 처리 중 예외가 발생해도 전체 동기화 작업은 중단되지 않습니다.
     */
    @Scheduled(fixedDelay = 60 * 1000) // 1분
    @Transactional
    public void flushProjectViews() {
        Set<String> keys = projectViewCountRedisPort.getAllViewCountKeys("PROJECT");

        for (String key : keys) {
            try {
                Long projectId = extractProjectId(key);
                Long count = projectViewCountRedisPort.getViewCount(projectId, "PROJECT");
                if (count > 0) {
                    projectRepositoryPort.increaseViewCount(projectId, count);
                    projectViewUpdatePort.increaseViewCount(projectId, count);
                    projectViewCountRedisPort.clearViewCount(projectId, "PROJECT");
                }
            } catch (Exception e) {
                log.error("프로젝트 조회수 플러시 실패 - key: {}", key, e);
            }
        }
    }

    /**
     * Redis 키에서 프로젝트 ID를 추출합니다.
     *
     * @param key "viewCount:PROJECT:<projectId>" 형식의 Redis 키 문자열
     * @return 추출된 프로젝트 ID
     * @throws IllegalArgumentException 키 형식이 올바르지 않거나 프로젝트 ID가 숫자가 아닐 경우 발생합니다.
     */
    private Long extractProjectId(String key) {
        // ex. viewCount:PROJECT:123
        String[] parts = key.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid key format: " + key);
        }
        try {
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid project ID in key: " + key, e);
        }
    }
}

