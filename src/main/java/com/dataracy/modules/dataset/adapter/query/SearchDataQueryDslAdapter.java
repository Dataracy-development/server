package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.query.predicates.DataDatePredicate;
import com.dataracy.modules.dataset.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.dataset.adapter.query.sort.DataSortBuilder;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchFilteredDataSetsPort;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
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
public class SearchDataQueryDslAdapter implements
        SearchFilteredDataSetsPort
{
    private final JPAQueryFactory queryFactory;

    private final QDataEntity data = QDataEntity.dataEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 필터 조건과 정렬 기준에 따라 데이터셋 목록을 페이지 단위로 조회합니다.
     * 각 데이터셋에 연관된 프로젝트 개수를 포함하여, 필터링, 정렬, 페이징이 모두 적용된 결과를 반환합니다.
     *
     * @param request 데이터셋 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지 정보 및 크기
     * @param sortType 데이터셋 정렬 기준
     * @return 데이터셋과 연결된 프로젝트 개수를 포함한 DTO의 페이지 객체
     */
    @Override
    public Page<DataWithProjectCountDto> searchByFilters(FilteringDataRequest request,
                                                         Pageable pageable,
                                                         DataSortType sortType) {
        Instant startTime = LoggerFactory.query()
                .logQueryStart("DataEntity", "[searchByFilters] 필터링된 데이터셋 목록 조회 시작. keyword=" + request.keyword());

        // 튜플에서 꺼낼 alias
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        // 각 데이터셋이 '전체' 몇 개 프로젝트에 연결됐는지 (distinct)
        var projectCountSub = JPAExpressions
                .select(projectData.project.id.countDistinct())
                .from(projectData)
                .where(projectData.dataId.eq(data.id));

        // 메인 쿼리: 조인/그룹바이 없이 깔끔 (메타데이터 1:1 이므로 fetch join OK)
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        ExpressionUtils.as(projectCountSub, projectCountPath)
                )
                .from(data)
                .join(data.metadata).fetchJoin()
                .where(buildFilterPredicates(request)) // null-safe predicate 메서드라면 그대로 사용
                .orderBy(DataSortBuilder.fromSortOption(sortType, projectCountPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(t -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(t.get(data)),
                        t.get(projectCountPath)
                ))
                .toList();

        // total: 집계/조인 불필요 (필터만 동일 적용)
        long total = Optional.ofNullable(
                queryFactory
                        .select(data.id.count())
                        .from(data)
                        .where(buildFilterPredicates(request))
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("DataEntity", "[searchByFilters] 필터링된 데이터셋 목록 조회 완료. keyword=" + request.keyword(), startTime);
        return new PageImpl<>(contents, pageable, total);
    }


    /**
     * FilteringDataRequest의 조건에 따라 데이터셋 필터링에 사용할 QueryDSL BooleanExpression 배열을 생성합니다.
     *
     * @param request 데이터셋 필터링에 필요한 키워드, 주제 ID, 데이터 소스 ID, 데이터 타입 ID, 연도 범위 등의 조건이 포함된 요청 객체
     * @return 각 필터 조건에 대응하는 BooleanExpression 배열
     */
    private BooleanExpression[] buildFilterPredicates(FilteringDataRequest request) {
        return new BooleanExpression[] {
                DataFilterPredicate.keywordContains(request.keyword()),
                DataFilterPredicate.topicIdEq(request.topicId()),
                DataFilterPredicate.dataSourceIdEq(request.dataSourceId()),
                DataFilterPredicate.dataTypeIdEq(request.dataTypeId()),
                DataDatePredicate.yearBetween(request.year())
        };
    }
}
