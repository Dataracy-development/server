package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectPopularOrderBuilder;
import com.dataracy.modules.project.adapter.query.sort.ProjectSortBuilder;
import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.application.port.query.ProjectQueryRepositoryPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ProjectQueryRepositoryPortAdapter implements ProjectQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;
    private final QProjectDataEntity dataEntity = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 ID에 해당하는 프로젝트를 조회하여 Optional로 반환합니다.
     *
     * 프로젝트와 연관된 부모 프로젝트 및 프로젝트 데이터도 함께 조회합니다.
     * 결과가 없을 경우 빈 Optional을 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 조회된 프로젝트 도메인 객체의 Optional, 없으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .distinct()
                .leftJoin(project.parentProject).fetchJoin()
                .leftJoin(project.projectDataEntities, dataEntity).fetchJoin()
                .leftJoin(project.childProjects).fetchJoin()
                .where(ProjectFilterPredicate.projectIdEq(projectId))
                .fetchOne();

        return Optional.ofNullable(ProjectEntityMapper.toFull(entity));
    }

    /**
     * 지정된 개수만큼 인기 프로젝트 목록을 조회합니다.
     *
     * @param size 반환할 프로젝트의 최대 개수
     * @return 인기 순으로 정렬된 프로젝트 도메인 객체 리스트
     */
    @Override
    public List<Project> findPopularProjects(int size) {
        return queryFactory
                .selectFrom(project)
                .orderBy(ProjectPopularOrderBuilder.popularOrder())
                .limit(size)
                .fetch()
                .stream()
                .map(ProjectEntityMapper::toMinimal)
                .toList();
    }

    private BooleanExpression[] buildFilterPredicates(ProjectFilterRequest request) {
        return new BooleanExpression[] {
                ProjectFilterPredicate.keywordContains(request.keyword()),
                ProjectFilterPredicate.topicIdEq(request.topicId()),
                ProjectFilterPredicate.analysisPurposeIdEq(request.analysisPurposeId()),
                ProjectFilterPredicate.dataSourceIdEq(request.dataSourceId()),
                ProjectFilterPredicate.authorLevelIdEq(request.authorLevelId())
        };
    }

    @Override
    public Page<Project> searchByFilters(ProjectFilterRequest request, Pageable pageable, ProjectSortType sortType) {

        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .distinct()
                .orderBy(ProjectSortBuilder.fromSortOption(sortType))
                .leftJoin(project.parentProject).fetchJoin()
                .leftJoin(project.childProjects).fetchJoin()
                .where(buildFilterPredicates(request))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Project> contents = entities.stream()
                .map(ProjectEntityMapper::toWithChildren)
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(project.count())
                        .from(project)
                        .distinct()
                        .where(buildFilterPredicates(request))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }
}
