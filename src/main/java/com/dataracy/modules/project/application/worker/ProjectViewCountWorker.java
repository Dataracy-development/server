package com.dataracy.modules.project.application.worker;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.out.view.ManageProjectViewCountPort;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class ProjectViewCountWorker {
    private final ManageProjectViewCountPort manageProjectViewCountPort;

    private final UpdateProjectViewPort updateProjectViewDbPort;
    private final ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    /**
     * ProjectViewCountWorker를 생성하고 동작에 필요한 포트를 주입합니다.
     *
     * <p>주입되는 포트:
     * - ManageProjectViewCountPort: Redis에 저장된 프로젝트 조회수 키/카운트를 관리.
     * - UpdateProjectViewPort (DB 어댑터로 주입): Redis에서 꺼낸 증가분을 메인 데이터베이스에 반영.
     * - ManageProjectProjectionTaskPort: 뷰 카운트 델타에 대한 프로젝션(예: 색인/비동기 작업) 작업을 큐에 등록.</p>
     */
    public ProjectViewCountWorker(
            ManageProjectViewCountPort manageProjectViewCountPort,
            @Qualifier("updateProjectViewDbAdapter") UpdateProjectViewPort updateProjectViewDbPort,
            ManageProjectProjectionTaskPort manageProjectProjectionTaskPort
    ) {
        this.manageProjectViewCountPort = manageProjectViewCountPort;
        this.updateProjectViewDbPort = updateProjectViewDbPort;
        this.manageProjectProjectionTaskPort = manageProjectProjectionTaskPort;
    }

    /**
     * Redis에 저장된 프로젝트별 조회수를 데이터베이스로 동기화하고 프로젝션 델타를 큐에 등록합니다.
     *
     * Redis에 저장된 모든 "PROJECT" 조회수 키를 조회하여 각 키에 대해 원자적으로 카운트를 팝(pop)합니다.
     * 팝한 값이 null이거나 0보다 작거나 같으면 무시하고, 양수인 경우에는 데이터베이스의 조회수를 증가시키고
     * 프로젝션(검색 색인 등) 업데이트를 위한 델타를 대기열에 등록합니다.
     *
     * 개별 프로젝트 처리 중 발생한 예외는 잡아서 로깅하며 다음 키 처리를 계속합니다.
     * 메서드는 스케줄러로 주기적으로 실행되며 트랜잭션 범위에서 동작합니다 (현재 fixedDelay = 20 * 1000).
     */
    @Scheduled(fixedDelay = 20 * 1000)
    @Transactional
    public void flushProjectViews() {
        LoggerFactory.scheduler().logStart("Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 시작");

        Set<String> keys = manageProjectViewCountPort.getAllViewCountKeys("PROJECT");
        for (String key : keys) {
            try {
                Long projectId = extractProjectId(key);
                Long count = manageProjectViewCountPort.popViewCount(projectId, "PROJECT"); // 원자적 pop
                if (count != null && count > 0) {
                    updateProjectViewDbPort.increaseViewCount(projectId, count);
                    manageProjectProjectionTaskPort.enqueueViewDelta(projectId, count);
                }
            } catch (Exception e) {
                LoggerFactory.scheduler().logError("Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 실패", e);
            }
        }
        LoggerFactory.scheduler().logComplete("Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 성공");
    }


    /**
     * "viewCount:PROJECT:<projectId>" 형식의 Redis 키에서 프로젝트 ID를 추출합니다.
     *
     * @param key 프로젝트 ID가 포함된 Redis 키 문자열
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
