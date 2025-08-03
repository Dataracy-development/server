package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("updateProjectViewDbAdapter")
@RequiredArgsConstructor
public class UpdateProjectViewDbAdapter implements UpdateProjectViewPort {
    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 지정된 프로젝트의 조회수를 주어진 수만큼 증가시킵니다.
     *
     * @param projectId 조회수를 증가시킬 프로젝트의 ID
     * @param count 증가시킬 조회수
     */
    @Override
    public void increaseViewCount(Long projectId, Long count) {
        projectJpaRepository.increaseViewCount(projectId, count);
        LoggerFactory.db().logUpdate("ProjectEntity", String.valueOf(projectId), "프로젝트 DB 조회수 " + count + "증가가 완료되었습니다.");
    }
}
