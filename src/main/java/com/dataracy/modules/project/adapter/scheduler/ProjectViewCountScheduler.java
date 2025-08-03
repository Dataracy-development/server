package com.dataracy.modules.project.adapter.scheduler;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.out.cache.CacheProjectViewCountPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
public class ProjectViewCountScheduler {

    private final CacheProjectViewCountPort cacheProjectViewCountPort;

    private final UpdateProjectViewPort updateProjectViewDbPort;
    private final UpdateProjectViewPort updateProjectViewEsPort;

    public ProjectViewCountScheduler(
            CacheProjectViewCountPort cacheProjectViewCountPort,
            @Qualifier("updateProjectViewDbAdapter") UpdateProjectViewPort updateProjectViewDbPort,
            @Qualifier("updateProjectViewEsAdapter") UpdateProjectViewPort updateProjectViewEsPort
    ) {
        this.cacheProjectViewCountPort = cacheProjectViewCountPort;
        this.updateProjectViewDbPort = updateProjectViewDbPort;
        this.updateProjectViewEsPort = updateProjectViewEsPort;
    }

    /**
     * Redis에 저장된 프로젝트별 조회수를 1분마다 메인 프로젝트 저장소와 프로젝트 뷰에 동기화합니다.
     *
     * 각 프로젝트의 조회수를 Redis에서 가져와 0보다 크면 저장소와 뷰에 반영한 뒤, 해당 Redis 조회수를 초기화합니다.
     * 개별 키 처리 중 예외가 발생해도 전체 동기화 작업은 중단되지 않습니다.
     */
    @Scheduled(fixedDelay = 60 * 1000) // 1분
    @Transactional
    public void flushProjectViews() {
        LoggerFactory.scheduler().logStart("Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 시작");

        Set<String> keys = cacheProjectViewCountPort.getAllViewCountKeys("PROJECT");
        for (String key : keys) {
            try {
                Long projectId = extractProjectId(key);
                Long count = cacheProjectViewCountPort.getViewCount(projectId, "PROJECT");
                if (count > 0) {
                    updateProjectViewDbPort.increaseViewCount(projectId, count);
                    updateProjectViewEsPort.increaseViewCount(projectId, count);
                    cacheProjectViewCountPort.clearViewCount(projectId, "PROJECT");
                }
            } catch (Exception e) {
                LoggerFactory.scheduler().logError("Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 실패", e);
            }
        }
        LoggerFactory.scheduler().logComplete("Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 성공");
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
            LoggerFactory.scheduler().logError("유효하지 않은 키 format: " + key);
            throw new IllegalArgumentException("유효하지 않은 키 format: " + key);
        }
        try {
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            LoggerFactory.scheduler().logError("유효하지 않은 project ID in key: " + key);
            throw new IllegalArgumentException("유효하지 않은 project ID in key: " + key, e);
        }
    }
}
