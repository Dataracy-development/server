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
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 ID에 해당하는 프로젝트를 조회하여 Optional로 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 조회된 프로젝트 도메인 객체의 Optional, 없으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .where(ProjectFilterPredicate.projectIdEq(projectId))
                .fetchOne();

        return Optional.ofNullable(ProjectEntityMapper.toMinimal(entity));
    }

    /**
     * 지정한 프로젝트 ID를 부모로 갖는 프로젝트가 존재하는지 여부를 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @return 해당 부모 프로젝트 ID를 가진 프로젝트가 하나라도 존재하면 true, 아니면 false
     */
    @Override
    public boolean existsByParentProjectId(Long projectId) {
        Integer result = queryFactory
                .selectOne()
                .from(project)
                .where(project.parentProject.id.eq(projectId))
                .fetchFirst();
        return result != null;
    }

    /**
     * 주어진 프로젝트 ID에 연결된 프로젝트 데이터가 존재하는지 여부를 반환합니다.
     *
     * @param projectId 존재 여부를 확인할 프로젝트의 ID
     * @return 프로젝트 데이터가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsProjectDataByProjectId(Long projectId) {
        Integer result = queryFactory
                .selectOne()
                .from(projectData)
                .where(projectData.project.id.eq(projectId))
                .fetchFirst();
        return result != null;
    }

    @Override
    public Page<Project> findContinueProjects(Long projectId, Pageable pageable) {
        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .orderBy(ProjectSortBuilder.fromSortOption(ProjectSortType.LATEST))
                .where(ProjectFilterPredicate.parentProjectIdEq(projectId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Project> contents = entities.stream()
                .map(ProjectEntityMapper::toMinimal
                )
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(project.count())
                        .from(project)
                        .distinct()
                        .where(ProjectFilterPredicate.parentProjectIdEq(projectId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 인기 순으로 정렬된 프로젝트 목록을 지정된 개수만큼 반환합니다.
     * 인기있는 프로젝트 목록 조회는 부모, 자식 프로젝트, 데이터셋을 반환하지 않아도 되므로 fetch join을 하지 않는다.
     *
     * @param size 반환할 프로젝트의 최대 개수
     * @return 인기 순으로 정렬된 최소 정보의 프로젝트 도메인 객체 리스트
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

    /**
     * 주어진 필터 요청을 기반으로 프로젝트 검색에 사용할 QueryDSL BooleanExpression 배열을 생성합니다.
     *
     * @param request 프로젝트 필터 조건이 담긴 요청 객체
     * @return 각 필터 조건에 해당하는 BooleanExpression 배열
     */
    private BooleanExpression[] buildFilterPredicates(ProjectFilterRequest request) {
        return new BooleanExpression[] {
                ProjectFilterPredicate.keywordContains(request.keyword()),
                ProjectFilterPredicate.topicIdEq(request.topicId()),
                ProjectFilterPredicate.analysisPurposeIdEq(request.analysisPurposeId()),
                ProjectFilterPredicate.dataSourceIdEq(request.dataSourceId()),
                ProjectFilterPredicate.authorLevelIdEq(request.authorLevelId())
        };
    }

    /**
     * 필터 조건, 페이지네이션, 정렬 기준에 따라 프로젝트 목록을 검색하여 페이지 형태로 반환합니다.
     * 자식 프로젝트(최대 2단계) 정보를 포함하여 결과를 제공합니다.
     *
     * @param request 프로젝트 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지네이션 정보
     * @param sortType 프로젝트 정렬 기준
     * @return 필터 및 정렬 조건에 맞는 프로젝트 목록과 전체 개수를 포함한 페이지 객체
     */
    @Override
    public Page<Project> searchByFilters(ProjectFilterRequest request, Pageable pageable, ProjectSortType sortType) {

        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .distinct()
                .orderBy(ProjectSortBuilder.fromSortOption(sortType))
                .leftJoin(project.childProjects).fetchJoin()
                .where(buildFilterPredicates(request))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Project> contents = entities.stream()
                .map(projectEntity ->
                        ProjectEntityMapper.toWithChildren(projectEntity, 2)
                )
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
