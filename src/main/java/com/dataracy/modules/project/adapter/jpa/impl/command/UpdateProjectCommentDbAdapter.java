package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectCommentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("updateProjectCommentDbAdapter")
@RequiredArgsConstructor
public class UpdateProjectCommentDbAdapter implements UpdateProjectCommentPort {
    private final ProjectJpaRepository projectJpaRepository;

    // Entity 상수 정의
    private static final String PROJECT_ENTITY = "ProjectEntity";

    /**
     * 지정된 프로젝트의 댓글 수를 1 증가시킵니다.
     *
     * @param projectId 댓글 수를 증가시킬 프로젝트의 ID
     */
    @Override
    public void increaseCommentCount(Long projectId) {
        projectJpaRepository.increaseCommentCount(projectId);
        LoggerFactory.db().logUpdate(PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 DB 댓글 1증가가 완료되었습니다.");
    }

    /**
     * 지정된 프로젝트의 댓글 수를 1 감소시킵니다.
     *
     * @param projectId 댓글 수를 감소시킬 프로젝트의 ID
     */
    @Override
    public void decreaseCommentCount(Long projectId) {
        projectJpaRepository.decreaseCommentCount(projectId);
        LoggerFactory.db().logUpdate(PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 DB 댓글 1감소가 완료되었습니다.");
    }
}
