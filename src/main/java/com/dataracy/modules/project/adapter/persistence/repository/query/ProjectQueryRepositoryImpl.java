package com.dataracy.modules.project.adapter.persistence.repository.query;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.persistence.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.persistence.repository.query.predicates.ProjectPredicateFactory;
import com.dataracy.modules.project.domain.model.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.dataracy.modules.project.adapter.persistence.entity.QProjectDataEntity.projectDataEntity;
import static com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity.projectEntity;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepositoryImpl implements ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Project> findProjectById(Long projectId) {
        ProjectEntity entity = queryFactory
                .selectFrom(projectEntity)
                .distinct()
                .leftJoin(projectEntity.parentProject).fetchJoin()
                .leftJoin(projectEntity.projectDataEntities, projectDataEntity).fetchJoin()
                .fetchJoin()
                .where(ProjectPredicateFactory.projectIdEq(projectId))
                .fetchOne();

        return Optional.ofNullable(ProjectEntityMapper.toDomain(entity));
    }
}
