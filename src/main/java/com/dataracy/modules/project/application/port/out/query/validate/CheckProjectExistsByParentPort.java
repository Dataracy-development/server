package com.dataracy.modules.project.application.port.out.query.validate;

public interface CheckProjectExistsByParentPort {
    /**
 * 주어진 프로젝트 ID를 부모로 하는 프로젝트가 하나 이상 존재하는지 확인합니다.
 *
 * @param projectId 부모 프로젝트의 ID
 * @return 하나 이상의 프로젝트가 해당 부모 프로젝트 ID를 가지고 있으면 true, 그렇지 않으면 false
 */
    boolean checkParentProjectExistsById(Long projectId);
}
