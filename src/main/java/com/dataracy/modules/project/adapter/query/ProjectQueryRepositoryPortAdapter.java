package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectDataFilterPredicate;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ProjectQueryRepositoryPortAdapter implements ProjectQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 ID에 해당하며 삭제되지 않은 프로젝트를 조회하여 Optional로 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 존재하는 경우 최소 정보가 매핑된 프로젝트의 Optional, 없으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchOne();

        return Optional.ofNullable(ProjectEntityMapper.toMinimal(entity));
    }

    /**
     * 지정한 프로젝트 ID를 부모로 갖는 자식 프로젝트가 삭제되지 않은 상태로 존재하는지 여부를 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @return 삭제되지 않은 자식 프로젝트가 하나 이상 존재하면 true, 없으면 false
     */
    @Override
    public boolean existsByParentProjectId(Long projectId) {
        Integer result = queryFactory
                .selectOne()
                .from(project)
                .where(
                        ProjectFilterPredicate.parentProjectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchFirst();
        return result != null;
    }

    /**
     * 주어진 프로젝트 ID에 연결된 삭제되지 않은 프로젝트 데이터가 존재하는지 반환합니다.
     *
     * @param projectId 존재 여부를 확인할 프로젝트의 ID
     * @return 삭제되지 않은 프로젝트 데이터가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsProjectDataByProjectId(Long projectId) {
        Integer result = queryFactory
                .selectOne()
                .from(projectData)
                .where(
                        ProjectDataFilterPredicate.projectIdEq(projectId),
                        ProjectDataFilterPredicate.notDeleted()
                )
                .fetchFirst();
        return result != null;
    }

    /**
     * 부모 프로젝트 ID에 해당하는 자식 프로젝트들을 최신순으로 페이지네이션하여 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 자식 프로젝트들의 페이지 결과
     */
    @Override
    public Page<Project> findContinueProjects(Long projectId, Pageable pageable) {
        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .orderBy(ProjectSortBuilder.fromSortOption(ProjectSortType.LATEST))
                .where(
                        ProjectFilterPredicate.parentProjectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Project> contents = entities.stream()
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(project.count())
                        .from(project)
                        .where(
                                ProjectFilterPredicate.parentProjectIdEq(projectId),
                                ProjectFilterPredicate.notDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 주어진 데이터 ID와 연관된 프로젝트들을 최신 생성일 순으로 페이징하여 조회합니다.
     *
     * @param dataId 연관된 데이터의 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 연관된 프로젝트들의 페이징 결과
     */
    @Override
    public Page<Project> findConnectedProjectsAssociatedWithData(Long dataId, Pageable pageable) {
        // 먼저 id 목록 조회 (페이징 포함)
        List<Long> projectIds = queryFactory
                .select(projectData.project.id)
                .from(projectData)
                .where(
                        ProjectDataFilterPredicate.dataIdEq(dataId),
                        ProjectDataFilterPredicate.notDeleted()
                )
                .orderBy(projectData.project.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (projectIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .where(project.id.in(projectIds))
                .fetch();

        Map<Long, ProjectEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        List<Project> contents = projectIds.stream()
                .map(entityMap::get)
                .filter(Objects::nonNull)
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(projectData.project.countDistinct())
                        .from(projectData)
                        .where(
                                ProjectDataFilterPredicate.dataIdEq(dataId),
                                ProjectDataFilterPredicate.notDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }


    /**
     * 인기 순으로 정렬된 프로젝트를 최대 지정된 개수만큼 조회합니다.
     *
     * 프로젝트의 최소 정보만 반환하며, 논리적으로 삭제되지 않은 프로젝트만 포함됩니다.
     *
     * @param size 반환할 프로젝트의 최대 개수
     * @return 인기 순으로 정렬된 프로젝트 도메인 객체 리스트
     */
    @Override
    public List<Project> findPopularProjects(int size) {
        return queryFactory
                .selectFrom(project)
                .where(
                        ProjectFilterPredicate.notDeleted()
                )
                .orderBy(ProjectPopularOrderBuilder.popularOrder())
                .limit(size)
                .fetch()
                .stream()
                .map(ProjectEntityMapper::toMinimal)
                .toList();
    }

    /**
     * 프로젝트 필터 요청에 따라 QueryDSL BooleanExpression 조건 배열을 생성합니다.
     *
     * @param request 프로젝트 검색에 적용할 필터 조건이 포함된 요청 객체
     * @return 필터 조건에 해당하는 QueryDSL BooleanExpression 배열
     */
    private BooleanExpression[] buildFilterPredicates(ProjectFilterRequest request) {
        return new BooleanExpression[] {
                ProjectFilterPredicate.keywordContains(request.keyword()),
                ProjectFilterPredicate.topicIdEq(request.topicId()),
                ProjectFilterPredicate.analysisPurposeIdEq(request.analysisPurposeId()),
                ProjectFilterPredicate.dataSourceIdEq(request.dataSourceId()),
                ProjectFilterPredicate.authorLevelIdEq(request.authorLevelId()),
                ProjectFilterPredicate.notDeleted()
        };
    }

    /**
     * 필터 조건, 페이지네이션, 정렬 기준에 따라 프로젝트 목록을 검색하여 페이지 형태로 반환합니다.
     * 결과에는 최대 2단계의 자식 프로젝트 정보가 포함됩니다.
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
                .orderBy(ProjectSortBuilder.fromSortOption(sortType))
                .leftJoin(project.childProjects).fetchJoin()
                .distinct()
                .where(
                        buildFilterPredicates(request)
                )
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
                        .where(
                                buildFilterPredicates(request)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }
}
