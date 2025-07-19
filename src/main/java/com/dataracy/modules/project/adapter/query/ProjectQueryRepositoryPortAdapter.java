package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectPopularOrderBuilder;
import com.dataracy.modules.project.application.port.query.ProjectQueryRepositoryPort;
import com.dataracy.modules.project.domain.model.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity.projectDataEntity;
import static com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity.projectEntity;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepositoryPortAdapter implements ProjectQueryRepositoryPort {

    private final JPAQueryFactory queryFactory;

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트를 조회하여 반환합니다.
     *
     * 프로젝트 엔티티와 연관된 부모 프로젝트 및 프로젝트 데이터 엔티티를 함께 조회하며,
     * 결과가 존재하지 않으면 빈 Optional을 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 조회된 프로젝트 도메인 객체의 Optional, 없으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        ProjectEntity entity = queryFactory
                .selectFrom(projectEntity)
                .distinct()
                .leftJoin(projectEntity.parentProject).fetchJoin()
                .leftJoin(projectEntity.projectDataEntities, projectDataEntity).fetchJoin()
                .where(ProjectFilterPredicate.projectIdEq(projectId))
                .fetchOne();

        return Optional.ofNullable(ProjectEntityMapper.toDomain(entity));
    }

    @Override
    public List<Project> findPopularProjects(int size) {
        return queryFactory
                .selectFrom(projectEntity)
                .orderBy(ProjectPopularOrderBuilder.popularOrder())
                .limit(size)
                .fetch()
                .stream()
                .map(ProjectEntityMapper::toDomain)
                .toList();
    }
}
