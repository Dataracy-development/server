package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectSortBuilder;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SearchProjectQueryDslAdapter implements
        SearchFilteredProjectsPort
{
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;

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
                .leftJoin(project.childProjects).fetchJoin()     // 1:N 페치조인 (2단계에서는 OK)
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
