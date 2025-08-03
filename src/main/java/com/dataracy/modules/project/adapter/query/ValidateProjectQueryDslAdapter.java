package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.query.predicates.ProjectDataFilterPredicate;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectDataExistsPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByParentPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ValidateProjectQueryDslAdapter implements
        CheckProjectExistsByParentPort,
        CheckProjectDataExistsPort
{
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 지정한 프로젝트 ID를 부모로 갖는 자식 프로젝트가 삭제되지 않은 상태로 존재하는지 여부를 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @return 삭제되지 않은 자식 프로젝트가 하나 이상 존재하면 true, 없으면 false
     */
    @Override
    public boolean checkParentProjectExistsById(Long projectId) {
        Integer result = queryFactory
                .selectOne()
                .from(project)
                .where(
                        ProjectFilterPredicate.parentProjectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchFirst();
        boolean isValid = result != null;
        if (isValid) {
            LoggerFactory.query().logExist("ProjectEntity", "[checkParentProjectExistsById] 지정한 프로젝트 ID를 부모로 갖는 자식 프로젝트 존재. projectId=" + projectId);
        } else {
            LoggerFactory.query().logExist("ProjectEntity", "[checkParentProjectExistsById] 지정한 프로젝트 ID를 부모로 갖는 자식 프로젝트 존재하지 않음. projectId=" + projectId);
        }
        return isValid;
    }

    /**
     * 주어진 프로젝트 ID에 연결된 삭제되지 않은 프로젝트 데이터가 존재하는지 반환합니다.
     *
     * @param projectId 존재 여부를 확인할 프로젝트의 ID
     * @return 삭제되지 않은 프로젝트 데이터가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean checkProjectDataExistsByProjectId(Long projectId) {
        Integer result = queryFactory
                .selectOne()
                .from(projectData)
                .where(
                        ProjectDataFilterPredicate.projectIdEq(projectId),
                        ProjectDataFilterPredicate.notDeleted()
                )
                .fetchFirst();
        boolean isValid = result != null;
        if (isValid) {
            LoggerFactory.query().logExist("ProjectEntity", "[checkProjectDataExistsByProjectId] 주어진 프로젝트 ID에 연결된 프로젝트 데이터가 존재. projectId=" + projectId);
        } else {
            LoggerFactory.query().logExist("ProjectEntity", "[checkProjectDataExistsByProjectId] 주어진 프로젝트 ID에 연결된 프로젝트 데이터가 존재하지 않음. projectId=" + projectId);
        }
        return isValid;
    }
}
