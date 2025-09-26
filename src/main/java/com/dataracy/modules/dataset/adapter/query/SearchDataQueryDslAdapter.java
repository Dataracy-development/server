package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        int queryCount = 0;

        // 3단계: 배치 처리 방식 (최적화)
        List<DataEntity> dataEntities = queryFactory
                .selectFrom(data)
                .leftJoin(data.metadata).fetchJoin()
                .where(buildFilterPredicates(request))
                .orderBy(DataSortBuilder.fromSortOption(sortType, null))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        queryCount++; // 메인 쿼리 (데이터 조회)

        // 배치로 프로젝트 수 조회 (N+1 문제 해결)
        List<Long> dataIds = dataEntities.stream().map(DataEntity::getId).toList();
        Map<Long, Long> projectCounts = getProjectCountsBatch(dataIds);
        queryCount++; // 배치 쿼리 1개

        // DTO 조합 및 프로젝트 수에 따른 메모리 정렬
        List<DataWithProjectCountDto> contents = dataEntities.stream()
                .map(entity -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(entity),
                        projectCounts.getOrDefault(entity.getId(), 0L)
                ))
                .sorted((a, b) -> { // 프로젝트 수 정렬은 메모리에서 처리
                    if (sortType == DataSortType.UTILIZE) { // UTILIZE는 프로젝트 수 기준 정렬을 의미
                        return Long.compare(b.countConnectedProjects(), a.countConnectedProjects());
                    }
                    return 0; // 다른 정렬 타입은 DB에서 이미 처리됨
                }) 
                .toList();

        // 총 개수 조회
        long total = Optional.ofNullable(queryFactory
                .select(data.id.count())
                .from(data)
                .where(buildFilterPredicates(request))
                .fetchOne()).orElse(0L);
        queryCount++; // 카운트 쿼리

        LoggerFactory.query().logQueryEnd("DataEntity", "[searchByFilters] 필터링된 데이터셋 목록 조회 완료. keyword=" + request.keyword() + ", queryCount=" + queryCount + ", dataSize=" + contents.size(), startTime);
        return new PageImpl<>(contents, pageable, total);
    }


    /**
     * 배치로 프로젝트 수를 조회합니다.
     */
    private Map<Long, Long> getProjectCountsBatch(List<Long> dataIds) {
        if (dataIds.isEmpty()) return Collections.emptyMap();
        
        return queryFactory
                .select(projectData.dataId, projectData.id.count())
                .from(projectData)
                .where(projectData.dataId.in(dataIds))
                .groupBy(projectData.dataId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(projectData.dataId),
                        tuple -> tuple.get(projectData.id.count())
                ));
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
