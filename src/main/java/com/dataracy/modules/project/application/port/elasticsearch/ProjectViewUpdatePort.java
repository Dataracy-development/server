package com.dataracy.modules.project.application.port.elasticsearch;

public interface ProjectViewUpdatePort {
    /**
 * 지정된 프로젝트의 조회수를 주어진 값만큼 증가시킵니다.
 *
 * @param projectId 조회수를 증가시킬 프로젝트의 ID
 * @param increment 증가할 조회수 값
 */
void increaseViewCount(Long projectId, Long increment);
}
