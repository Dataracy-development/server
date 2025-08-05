package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectPopularOrderBuilder;
import com.dataracy.modules.project.adapter.query.sort.ProjectSortBuilder;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.port.out.query.search.SearchFilteredProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
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
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GetProjectQueryDslAdapter implements
        GetPopularProjectsPort,
        SearchFilteredProjectsPort
{
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;

    /**
     * 지정된 개수만큼 인기 순으로 정렬된 프로젝트 목록을 반환합니다.
     *
     * 논리적으로 삭제되지 않은 프로젝트만 포함되며, 각 프로젝트는 최소 정보만을 담고 있습니다.
     *
     * @param size 반환할 프로젝트의 최대 개수
     * @return 인기 순으로 정렬된 프로젝트 도메인 객체 리스트
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
    public Page<Project> searchByFilters(FilteringProjectRequest request, Pageable pageable, ProjectSortType sortType) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[searchByFilters] 프로젝트 필터링 조회 시작. keyword=" + request.keyword());

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

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[searchByFilters] 프로젝트 필터링 조회 완료. keyword=" + request.keyword(), startTime);
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
