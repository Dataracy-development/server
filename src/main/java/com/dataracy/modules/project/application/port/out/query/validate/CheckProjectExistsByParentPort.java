package com.dataracy.modules.project.application.port.out.query.validate;

public interface CheckProjectExistsByParentPort {
    /**
     * 지정된 프로젝트 ID를 부모로 갖는 프로젝트가 존재하는지 여부를 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @return 해당 부모 프로젝트 ID를 가진 프로젝트가 하나 이상 존재하면 true, 아니면 false
     */
    boolean checkParentProjectExistsById(Long projectId);
}
