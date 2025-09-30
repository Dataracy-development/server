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
import java.util.Map;
import java.util.HashMap;import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SearchProjectQueryDslAdapter implements
        SearchFilteredProjectsPort
{
    private final JPAQueryFactory queryFactory;

    // Entity 상수 정의
    private static final String PROJECT_ENTITY = "ProjectEntity";

    private static final QProjectEntity project = QProjectEntity.projectEntity;

    /**
     * 필터·정렬·페이징 조건에 따라 프로젝트를 조회하여 Page로 반환합니다.
     *
     * 루트 프로젝트 기준으로 페이징(컬렉션 조인 제외)을 수행한 뒤 해당 루트들만을 다시 로드하면서
     * 1단계 자식은 fetch join으로 함께 가져오고 도메인 변환 시 최대 2단계 자식까지 포함합니다.
     * ID 기반 조회로 인해 보장되지 않는 결과 순서는 원래의 페이지 순서로 복원합니다. 전체 개수(total)는
     * 루트 프로젝트 기준으로 계산합니다.
     *
     * @param request 필터링 조건을 담은 요청 객체(키워드, 토픽, 목적, 데이터소스, 저자레벨 등)
     * @param pageable 페이징 정보(offset, pageSize 등)
     * @param sortType 결과 정렬 방식
     * @return 필터·정렬·페이징 결과를 담은 Page<Project> (contents는 도메인 객체, total은 루트 기준 카운트)
     */
    @Override
    public Page<Project> searchByFilters(FilteringProjectRequest request,
                                         Pageable pageable,
                                         ProjectSortType sortType) {
        Instant start = LoggerFactory.query().logQueryStart(PROJECT_ENTITY,
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
            LoggerFactory.query().logQueryEnd(PROJECT_ENTITY, "[searchByFilters] 결과 0", start);
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

        LoggerFactory.query().logQueryEnd(PROJECT_ENTITY, "[searchByFilters] 완료", start);
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
