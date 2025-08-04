package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.dataset.adapter.query.sort.DataPopularOrderBuilder;
import com.dataracy.modules.dataset.adapter.query.sort.DataSortBuilder;
import com.dataracy.modules.dataset.application.dto.response.read.DataGroupCountResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.out.query.read.*;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.reference.adapter.jpa.entity.QTopicEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
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
public class ReadDataQueryDslAdapter implements
        FindDataPort,
        FindDataWithMetadataPort,
        GetConnectedDataSetsPort,
        GetDataGroupCountPort,
        GetRecentDataSetsPort,
        GetPopularDataSetsPort
{
    private final JPAQueryFactory queryFactory;

    private final QDataEntity data = QDataEntity.dataEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;
    private final QTopicEntity topic = QTopicEntity.topicEntity;

    /**
     * 데이터 ID로 해당 데이터를 조회하여 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 식별자
     * @return 데이터가 존재하면 도메인 Data 객체를 포함한 Optional, 없으면 빈 Optional
     */
    @Override
    public Optional<Data> findDataById(Long dataId) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[findDataById] 아이디를 통해 데이터셋 조회 시작. dataId=" + dataId);
        DataEntity entity = queryFactory
                .selectFrom(data)
                .where(
                        DataFilterPredicate.dataIdEq(dataId)
                )
                .fetchOne();
        Optional<Data> data = Optional.ofNullable(entity).map(DataEntityMapper::toDomain);
        LoggerFactory.query().logQueryEnd("DataEntity", "[findDataById] 아이디를 통해 데이터셋 조회 완료. dataId=" + dataId, startTime);
        return data;
    }

    /**
     * 데이터 ID로 데이터를 조회하고, 연관된 메타데이터를 즉시 로딩하여 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터와 메타데이터를 포함하는 도메인 객체의 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<Data> findDataWithMetadataById(Long dataId) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[findDataWithMetadataById] 아이디를 통해 삭제된 데이터셋을 포함한 데이터셋 조회 시작. dataId=" + dataId);
        DataEntity entity = queryFactory
                .selectFrom(data)
                .leftJoin(data.metadata).fetchJoin()
                .where(
                        DataFilterPredicate.dataIdEq(dataId)
                )
                .fetchOne();
        Optional<Data> data = Optional.ofNullable(entity).map(DataEntityMapper::toDomain);
        LoggerFactory.query().logQueryEnd("DataEntity", "[findDataWithMetadataById] 아이디를 통해 삭제된 데이터셋을 포함한 데이터셋 조회 완료. dataId=" + dataId, startTime);
        return data;
    }

    /**
     * 데이터셋을 주제별로 그룹화하여 각 주제에 속한 데이터셋의 개수를 반환합니다.
     *
     * @return 각 주제의 ID, 이름, 데이터셋 개수를 포함하는 CountDataGroupResponse 객체 리스트
     */
    @Override
    public List<DataGroupCountResponse> getDataGroupCount() {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[getDataGroupCount] 토픽별 데이터셋 개수 조회 시작.");

        List<DataGroupCountResponse> dataGroupCountResponses = queryFactory
                .select(Projections.constructor(DataGroupCountResponse.class,
                        topic.id,
                        topic.label,
                        data.count()
                ))
                .from(data)
                .join(topic).on(data.topicId.eq(topic.id))
                .groupBy(topic.id, topic.label)
                .fetch();

        LoggerFactory.query().logQueryEnd("DataEntity", "[getDataGroupCount] 토픽별 데이터셋 개수 조회 완료.", startTime);
        return dataGroupCountResponses;
    }

    /**
     * 지정된 프로젝트에 연결된 데이터셋을 최신순으로 조회하여 프로젝트 연결 개수와 함께 페이지 형태로 반환합니다.
     *
     * @param projectId 연결된 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 프로젝트에 연결된 데이터셋과 각 데이터셋의 프로젝트 연결 개수를 포함하는 페이지 객체
     */
    @Override
    public Page<DataWithProjectCountDto> getConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[getConnectedDataSetsAssociatedWithProject] 지정된 프로젝트에 연결된 데이터셋 목록 조회 시작. projectId=" + projectId);

        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        projectData.id.count().as(projectCountPath)
                )
                .from(data)
                .innerJoin(projectData).on(projectData.dataId.eq(data.id)
                        .and(projectData.project.id.eq(projectId)))
                .leftJoin(data.metadata).fetchJoin()
                .groupBy(data.id)
                .orderBy(DataSortBuilder.fromSortOption(DataSortType.LATEST, null))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(tuple -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(tuple.get(data)),
                        tuple.get(projectCountPath)
                ))
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(data.id.countDistinct())
                        .from(data)
                        .innerJoin(projectData).on(projectData.dataId.eq(data.id)
                                .and(projectData.project.id.eq(projectId)))
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("DataEntity", "[getConnectedDataSetsAssociatedWithProject] 지정된 프로젝트에 연결된 데이터셋 목록 조회 시작. projectId=" + projectId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 최신 데이터셋을 지정된 개수만큼 조회합니다.
     *
     * @param size 반환할 데이터셋의 최대 개수
     * @return 최신순으로 정렬된 데이터 도메인 객체 리스트
     */
    @Override
    public List<Data> getRecentDataSets(int size) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[getRecentDataSets] 최신 데이터셋 목록 조회 시작. size=" + size);

        List<DataEntity> dataEntities = queryFactory
                .selectFrom(data)
                .orderBy(DataSortBuilder.fromSortOption(DataSortType.LATEST, null))
                .limit(size)
                .fetch();

        List<Data> dataSets = dataEntities.stream()
                .map(DataEntityMapper::toDomain)
                .toList();
        LoggerFactory.query().logQueryEnd("DataEntity", "[getRecentDataSets] 최신 데이터셋 목록 조회 완료. size=" + size, startTime);
        return dataSets;
    }

    /**
     * 인기도 점수를 기준으로 상위 데이터셋 목록과 각 데이터셋에 연결된 프로젝트 수를 조회합니다.
     *
     * @param size 반환할 데이터셋의 최대 개수
     * @return 각 데이터셋과 해당 데이터셋에 연결된 프로젝트 수를 포함하는 DTO 리스트
     */
    @Override
    public List<DataWithProjectCountDto> getPopularDataSets(int size) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[searchPopularDataSets] 인기있는 데이터셋 목록 조회 시작. size=" + size);

        NumberExpression<Long> projectCountExpr = projectData.id.count();
        NumberExpression<Double> score = DataPopularOrderBuilder.popularScore(data, projectCountExpr);

        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        projectCountExpr
                )
                .from(data)
                .leftJoin(projectData).on(projectData.dataId.eq(data.id))
                .leftJoin(data.metadata).fetchJoin()
                .groupBy(data.id)
                .orderBy(score.desc())
                .limit(size)
                .fetch();

        List<DataWithProjectCountDto> dataWithProjectCountDtos = tuples.stream()
                .map(tuple -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(tuple.get(data)),
                        tuple.get(projectCountExpr)
                ))
                .toList();
        LoggerFactory.query().logQueryEnd("DataEntity", "[searchPopularDataSets] 인기있는 데이터셋 목록 조회 시작. size=" + size, startTime);
        return dataWithProjectCountDtos;
    }
}
