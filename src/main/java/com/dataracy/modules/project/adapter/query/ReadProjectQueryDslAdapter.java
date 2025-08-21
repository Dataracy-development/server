package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectDataFilterPredicate;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectPopularOrderBuilder;
import com.dataracy.modules.project.adapter.query.sort.ProjectSortBuilder;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.application.port.out.query.read.FindConnectedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindContinuedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchFilteredProjectsPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReadProjectQueryDslAdapter implements
        FindProjectPort,
        FindContinuedProjectsPort,
        FindConnectedProjectsPort,
        GetPopularProjectsPort,
        SearchFilteredProjectsPort
{
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 ID에 해당하며 삭제되지 않은 프로젝트를 최소 정보로 조회하여 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트가 존재하면 최소 정보가 매핑된 Optional<Project>, 없으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findProjectById] 아이디를 통해 삭제되지 않은 프로젝트를 조회 시작. projectId=" + projectId);
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchOne();
        Optional<Project> savedProject = Optional.ofNullable(ProjectEntityMapper.toMinimal(entity));
        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectById] 아이디를 통해 삭제되지 않은 프로젝트를 조회 완료. projectId=" + projectId, startTime);
        return savedProject;
    }

    /**
         * 지정한 프로젝트 ID에 해당하는 삭제되지 않은 프로젝트와 해당 프로젝트에 연결된 데이터셋 ID 목록을 조회합니다.
         *
         * 상세:
         * - 프로젝트가 존재하지 않으면 Optional.empty()를 반환합니다.
         * - 존재하면 프로젝트(부모 프로젝트 정보 포함)와 그에 연결된 데이터셋 ID 목록을 ProjectWithDataIdsResponse로 감싸 반환합니다.
         *
         * @param projectId 조회할 프로젝트의 ID
         * @return 프로젝트(부모 포함)과 연결된 데이터셋 ID 목록을 포함한 Optional. 프로젝트가 없으면 Optional.empty()
         */
    @Override
    public Optional<ProjectWithDataIdsResponse> findProjectWithDataById(Long projectId) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findProjectWithDataById] 아이디를 통해 삭제되지 않은 프로젝트를 연결된 데이터셋과 함께 조회 시작. projectId=" + projectId);
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .leftJoin(project.parentProject)
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchOne();

        if (entity == null) {
            LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectWithDataById] 해당하는 프로젝트 리소스가 존재하지 않습니다. projectId=" + projectId, startTime);
            return Optional.empty();
        }

        // 연결된 dataId 목록
        List<Long> dataIds = queryFactory
                .select(projectData.dataId)
                .from(projectData)
                .where(
                        projectData.project.id.eq(projectId),
                        ProjectDataFilterPredicate.notDeleted()
                )
                .fetch();

        Optional<ProjectWithDataIdsResponse> projectWithDataIdsResponse = Optional.of(new ProjectWithDataIdsResponse(
                ProjectEntityMapper.toWithParent(entity),
                dataIds
        ));
        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectWithDataById] 아이디를 통해 삭제되지 않은 프로젝트를 연결된 데이터셋과 함께 조회 완료. projectId=" + projectId, startTime);
        return projectWithDataIdsResponse;
    }

    /**
     * 지정된 부모 프로젝트 ID에 속한 자식 프로젝트들을 최신순으로 페이지네이션하여 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @param pageable 결과 페이지네이션 정보
     * @return 자식 프로젝트들의 페이지네이션된 목록
     */
    @Override
    public Page<Project> findContinuedProjects(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findContinuedProjects] 이어가기 프로젝트 목록 조회 시작. projectId=" + projectId);
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

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findContinuedProjects] 이어가기 프로젝트 목록 조회 완료. projectId=" + projectId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 지정된 데이터 ID와 연결된 프로젝트들을 페이징하여 조회합니다.
     *
     * <p>프로젝트는 연결된 ProjectData 엔티티를 기준으로 프로젝트 생성일(createdAt) 내림차순으로 정렬되어 반환됩니다.
     * 조회는 soft-delete(삭제 플래그가 설정되지 않은) 된 연결만 대상으로 하며, 결과 콘텐츠는 최소 정보(minimal) 형태로 매핑됩니다.
     * 총건수는 해당 데이터와 연결된 서로 다른 프로젝트 수(distinct)를 기준으로 계산됩니다.</p>
     *
     * @param dataId 조회할 데이터(데이터셋)의 ID
     * @param pageable 페이지 번호·크기 및 정렬 정보를 포함한 페이징 파라미터
     * @return 페이징된 프로젝트 목록을 담은 Page 객체(정렬: 프로젝트 생성일 내림차순, 총건수는 distinct 기준)
     */
    @Override
    public Page<Project> findConnectedProjectsAssociatedWithDataset(Long dataId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findConnectedProjectsAssociatedWithDataset] 데이터셋과 연결된 프로젝트 목록 조회 시작. dataId=" + dataId);

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

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findConnectedProjectsAssociatedWithDataset] 데이터셋과 연결된 프로젝트 목록 조회 완료. dataId=" + dataId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 지정된 개수만큼 인기 순으로 정렬된 프로젝트의 최소 정보 목록을 반환합니다.
     *
     * 논리적으로 삭제되지 않은 프로젝트만 포함되며, 내부적으로 인기 순 정렬을 적용하고 결과를 도메인 모델의 최소 표현(Project)으로 매핑합니다.
     *
     * @param size 반환할 최대 프로젝트 개수
     * @return 인기 순으로 정렬된 Project 도메인 객체의 리스트
     */
    @Override
    public List<Project> getPopularProjects(int size) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[getPopularProjects] 인기있는 프로젝트 목록 조회 시작.");
        List<Project> popularProjects =  queryFactory
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

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[getPopularProjects] 인기있는 프로젝트 목록 조회 완료.", startTime);
        return popularProjects;
    }

    /**
     * 필터 조건, 페이지네이션, 정렬 기준에 따라 프로젝트 목록을 검색하여 페이지 형태로 반환합니다.
     * 최대 2단계의 자식 프로젝트 정보가 포함됩니다.
     *
     * @param request 프로젝트 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지네이션 정보
     * @param sortType 프로젝트 정렬 기준
     * @return 필터 및 정렬 조건에 맞는 프로젝트 목록과 전체 개수를 포함한 페이지 객체
     */
    @Override
    public Page<Project> searchByFilters(FilteringProjectRequest request,
                                         Pageable pageable,
                                         ProjectSortType sortType) {
        Instant start = LoggerFactory.query().logQueryStart("ProjectEntity",
                "[searchByFilters] 프로젝트 필터링 조회 시작. keyword=" + request.keyword());

        // 루트 ID만 페이징 (컬렉션 조인/페치 금지)
        List<Long> pageIds = queryFactory
                .select(project.id)
                .from(project)
                .where(buildFilterPredicates(request))
                .orderBy(ProjectSortBuilder.fromSortOption(sortType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (pageIds.isEmpty()) {
            LoggerFactory.query().logQueryEnd("ProjectEntity", "[searchByFilters] 결과 0", start);
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 부모 + 1단계 자식만 한 번에 로딩 (여기서는 페이징 없음 → fetch join 가능 : 안전)
        List<ProjectEntity> parentsWithChildren = queryFactory
                .selectFrom(project)
                .distinct()                                      // 루트 중복 방지
                .leftJoin(project.childProjects).fetchJoin()     // ✅ 1:N 페치조인 (2단계에서는 OK)
                .where(project.id.in(pageIds))
                .fetch();

        // ID 페이징에서의 정렬 순서 복원 (IN 결과는 순서 비보장)
        var order = new HashMap<Long, Integer>(pageIds.size());
        for (int i = 0; i < pageIds.size(); i++) {
            order.put(pageIds.get(i), i);
        }
        parentsWithChildren.sort(Comparator.comparingInt(e -> order.get(e.getId())));

        // 도메인 변환 (자식만 포함)
        List<Project> contents = parentsWithChildren.stream()
                .map(e -> ProjectEntityMapper.toWithChildren(e, 2))
                .toList();

        // total (루트만 카운트, 컬렉션 조인 불필요)
        long total = Optional.ofNullable(
                queryFactory.select(project.id.count())
                        .from(project)
                        .where(buildFilterPredicates(request))
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[searchByFilters] 완료", start);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 프로젝트 필터링 요청에 따라 QueryDSL BooleanExpression 조건 배열을 반환합니다.
     *
     * FilteringProjectRequest의 각 필드(키워드, 주제, 분석 목적, 데이터 소스, 작성자 레벨)에 해당하는 조건과
     * 삭제되지 않은 프로젝트만을 포함하는 조건을 생성하여 배열로 제공합니다.
     *
     * @param request 프로젝트 검색에 사용할 다양한 필터 조건이 포함된 요청 객체
     * @return 요청 조건에 맞는 QueryDSL BooleanExpression 조건 배열
     */
    private BooleanExpression[] buildFilterPredicates(FilteringProjectRequest request) {
        return new BooleanExpression[] {
                ProjectFilterPredicate.keywordContains(request.keyword()),
                ProjectFilterPredicate.topicIdEq(request.topicId()),
                ProjectFilterPredicate.analysisPurposeIdEq(request.analysisPurposeId()),
                ProjectFilterPredicate.dataSourceIdEq(request.dataSourceId()),
                ProjectFilterPredicate.authorLevelIdEq(request.authorLevelId()),
                ProjectFilterPredicate.notDeleted()
        };
    }
}
