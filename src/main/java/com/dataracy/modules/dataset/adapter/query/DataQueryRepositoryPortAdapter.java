package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.query.predicates.DataDatePredicate;
import com.dataracy.modules.dataset.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.dataset.adapter.query.sort.DataPopularOrderBuilder;
import com.dataracy.modules.dataset.adapter.query.sort.DataSortBuilder;
import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.response.CountDataGroupResponse;
import com.dataracy.modules.dataset.application.dto.response.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.query.DataQueryRepositoryPort;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.reference.adapter.jpa.entity.QTopicEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
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

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class DataQueryRepositoryPortAdapter implements DataQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QDataEntity data = QDataEntity.dataEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;
    private final QTopicEntity topic = QTopicEntity.topicEntity;

    /**
     * 주어진 데이터 ID에 해당하는 데이터를 조회하여 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 식별자
     * @return 데이터가 존재하면 도메인 Data 객체를, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<Data> findDataById(Long dataId) {
        DataEntity entity = queryFactory
                .selectFrom(data)
                .where(DataFilterPredicate.dataIdEq(dataId))
                .fetchOne();

        return Optional.ofNullable(DataEntityMapper.toDomain(entity));
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터 엔티티를 조회하며, 연관된 메타데이터를 즉시 로딩하여 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터와 메타데이터를 포함하는 도메인 객체의 Optional, 존재하지 않으면 빈 Optional 반환
     */
    @Override
    public Optional<Data> findDataWithMetadataById(Long dataId) {
        DataEntity entity = queryFactory
                .selectFrom(data)
                .leftJoin(data.metadata).fetchJoin()
                .where(DataFilterPredicate.dataIdEq(dataId))
                .fetchOne();

        return Optional.ofNullable(DataEntityMapper.toDomain(entity));
    }

    /**
     * 데이터셋의 인기도를 기준으로 상위 데이터셋 목록을 조회합니다.
     *
     * 각 데이터셋에 연결된 프로젝트 수를 함께 반환하며, 결과는 지정한 개수만큼 제한됩니다.
     *
     * @param size 반환할 데이터셋의 최대 개수
     * @return 프로젝트 수와 함께 매핑된 인기 데이터셋 DTO 리스트
     */
    @Override
    public List<DataWithProjectCountDto> findPopularDataSets(int size) {
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        JPAExpressions
                                .select(projectData.count())
                                .from(projectData)
                                .where(projectData.dataId.eq(data.id))
                )
                .from(data)
                .leftJoin(data.metadata).fetchJoin()
                .orderBy(DataPopularOrderBuilder.popularOrder(data, projectData))
                .limit(size)
                .fetch();
        return tuples.stream()
                .map(tuple -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(tuple.get(data)),
                        tuple.get(1, Long.class)
                ))
                .toList();
    }

    private BooleanExpression[] buildFilterPredicates(DataFilterRequest request) {
        return new BooleanExpression[] {
                DataFilterPredicate.keywordContains(request.keyword()),
                DataFilterPredicate.topicIdEq(request.topicId()),
                DataFilterPredicate.dataSourceIdEq(request.dataSourceId()),
                DataFilterPredicate.dataTypeIdEq(request.dataTypeId()),
                DataDatePredicate.yearBetween(request.year())
        };
    }

    @Override
    public Page<DataWithProjectCountDto> searchByFilters(DataFilterRequest request, Pageable pageable, DataSortType sortType) {
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        projectData.count().as(projectCountPath)
                )
                .from(data)
                .leftJoin(projectData).on(projectData.dataId.eq(data.id))
                .groupBy(data.id)
                .orderBy(DataSortBuilder.fromSortOption(sortType, projectCountPath))
                .leftJoin(data.metadata).fetchJoin()
                .where(buildFilterPredicates(request))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(tuple -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(tuple.get(data)),
                        tuple.get(1, Long.class)
                ))
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(data.count())
                        .from(data)
                        .distinct()
                        .where(buildFilterPredicates(request))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }

    @Override
    public List<Data> findRecentDataSets(int size) {
        List<DataEntity> dataEntities = queryFactory
                .selectFrom(data)
                .orderBy(DataSortBuilder.fromSortOption(DataSortType.LATEST, null))
                .limit(size)
                .fetch();

        return dataEntities.stream()
                .map(DataEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<CountDataGroupResponse> countDataGroups() {
        return queryFactory
                .select(Projections.constructor(CountDataGroupResponse.class,
                        topic.id,
                        topic.label,
                        data.count()
                ))
                .from(data)
                .join(topic).on(data.topicId.eq(topic.id))
                .groupBy(topic.id, topic.label)
                .fetch();
    }
}
