package com.dataracy.modules.project.adapter.jpa.impl.validation;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ValidateProjectDbAdapter implements CheckProjectExistsByIdPort {
    private final ProjectJpaRepository projectJpaRepository;

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트의 존재 여부를 반환합니다.
     *
     * @param projectId 확인할 프로젝트의 ID
     * @return 프로젝트가 존재하면 true, 존재하지 않으면 false
     */
    @Override
    public boolean checkProjectExistsById(Long projectId) {
        boolean isExist = projectJpaRepository.existsById(projectId);
        LoggerFactory.db().logExist("ProjectEntity", "해당 프로젝트 존재 유무 확인이 완료되었습니다. projectId=" + projectId + ", exists=" + isExist);
        return isExist;
    }
}
