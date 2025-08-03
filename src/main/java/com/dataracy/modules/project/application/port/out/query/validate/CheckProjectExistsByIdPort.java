package com.dataracy.modules.project.application.port.out.query.validate;

public interface CheckProjectExistsByIdPort {
    /**
     * 지정한 프로젝트 ID에 해당하는 프로젝트의 존재 여부를 확인합니다.
     *
     * @param projectId 존재 여부를 확인할 프로젝트의 ID
     * @return 프로젝트가 존재하면 true, 존재하지 않으면 false
     */
    boolean checkProjectExistsById(Long projectId);
}
