package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.query.predicates.DataDatePredicate;
import com.dataracy.modules.dataset.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.dataset.adapter.query.sort.DataPopularOrderBuilder;
import com.dataracy.modules.dataset.adapter.query.sort.DataSortBuilder;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchFilteredDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.query.read.GetPopularDataSetsPort;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
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
     *
     * 각 데이터셋에 연관된 프로젝트 개수를 포함하여, 필터링, 정렬, 페이징이 모두 적용된 결과를 반환합니다.
     *
     * @param request 데이터셋 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지 정보 및 크기
     * @param sortType 데이터셋 정렬 기준
     * @return 데이터셋과 연관 프로젝트 개수를 포함한 DTO의 페이지 객체
     */
    @Override
    public Page<DataWithProjectCountDto> searchByFilters(FilteringDataRequest request, Pageable pageable, DataSortType sortType) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[searchByFilters] 필터링된 데이터셋 목록 조회 시작. keyword=" + request.keyword());

        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        projectData.id.count().as(projectCountPath)
                )
                .from(data)
                .leftJoin(projectData).on(projectData.dataId.eq(data.id))
                .leftJoin(data.metadata).fetchJoin()
                .where(buildFilterPredicates(request))
                .groupBy(data.id)
                .orderBy(DataSortBuilder.fromSortOption(sortType, projectCountPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(tuple -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(tuple.get(data)),
                        tuple.get(projectCountPath)
                ))
                .toList();

        long total = Optional.ofNullable(queryFactory
                .select(data.id.countDistinct())
                .from(data)
                .leftJoin(projectData).on(projectData.dataId.eq(data.id))
                .where(buildFilterPredicates(request))
                .fetchOne()).orElse(0L);

        LoggerFactory.query().logQueryEnd("DataEntity", "[searchByFilters] 필터링된 데이터셋 목록 조회 시작. keyword=" + request.keyword(), startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 주어진 DataFilterRequest의 필터 조건에 따라 QueryDSL BooleanExpression 배열을 생성합니다.
     *
     * @param request 데이터 필터링 조건이 포함된 요청 객체
     * @return 각 필터 조건에 해당하는 BooleanExpression 배열
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
