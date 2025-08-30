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
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
        FindConnectedDataSetsPort,
        GetDataGroupCountPort,
        GetRecentDataSetsPort,
        GetPopularDataSetsPort,
        FindUserDataSetsPort
{
    private final JPAQueryFactory queryFactory;

    private final QDataEntity data = QDataEntity.dataEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;
    private final QTopicEntity topic = QTopicEntity.topicEntity;

    /**
     * 주어진 데이터 ID로 삭제되지 않은 데이터셋을 조회하여 반환합니다.
     *
     * <p>데이터가 존재하면 도메인 객체를 담은 Optional을, 없으면 빈 Optional을 반환합니다.</p>
     *
     * @param dataId 조회할 데이터의 고유 ID
     * @return 데이터가 존재하면 해당 Data 도메인 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<Data> findDataById(Long dataId) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[findDataById] 아이디를 통해 데이터셋 조회 시작. dataId=" + dataId);
        DataEntity entity = queryFactory
                .selectFrom(data)
                .where(
                        DataFilterPredicate.notDeleted(),
                        DataFilterPredicate.dataIdEq(dataId)
                )
                .fetchOne();
        Optional<Data> data = Optional.ofNullable(entity).map(DataEntityMapper::toDomain);
        LoggerFactory.query().logQueryEnd("DataEntity", "[findDataById] 아이디를 통해 데이터셋 조회 완료. dataId=" + dataId, startTime);
        return data;
    }

    /**
     * ID로 데이터와 연관된 메타데이터를 함께 조회합니다. 삭제된 데이터는 조회 대상에서 제외됩니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터(메타데이터 포함)를 담은 Optional — 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<Data> findDataWithMetadataById(Long dataId) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity", "[findDataWithMetadataById] 아이디를 통해 삭제된 데이터셋을 포함한 데이터셋 조회 시작. dataId=" + dataId);
        DataEntity entity = queryFactory
                .selectFrom(data)
                .leftJoin(data.metadata).fetchJoin()
                .where(
                        DataFilterPredicate.notDeleted(),
                        DataFilterPredicate.dataIdEq(dataId)
                )
                .fetchOne();
        Optional<Data> data = Optional.ofNullable(entity).map(DataEntityMapper::toDomain);
        LoggerFactory.query().logQueryEnd("DataEntity", "[findDataWithMetadataById] 아이디를 통해 삭제된 데이터셋을 포함한 데이터셋 조회 완료. dataId=" + dataId, startTime);
        return data;
    }

    /**
     * 데이터셋을 주제별로 그룹화하여 각 주제에 속한 데이터셋의 개수를 조회합니다.
     *
     * @return 각 주제의 ID, 이름, 데이터셋 개수를 포함하는 DataGroupCountResponse 객체 리스트
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
                .join(topic).on(
                        data.topicId.eq(topic.id),
                        data.isDeleted.isFalse()
                )
                .groupBy(topic.id, topic.label)
                .fetch();

        LoggerFactory.query().logQueryEnd("DataEntity", "[getDataGroupCount] 토픽별 데이터셋 개수 조회 완료.", startTime);
        return dataGroupCountResponses;
    }

    /**
     * 지정된 프로젝트에 연결된 데이터셋을 조회하여, 각 데이터셋별로 연결된 프로젝트 수를 함께 페이지 형태로 반환합니다.
     *
     * <p>결과는 최신순으로 정렬되며 데이터의 메타정보는 fetchJoin으로 함께 로드됩니다. 각 항목에는 도메인 Data와
     * 해당 데이터에 연결된 프로젝트의 개수가 포함됩니다. 전체 집계(total)는 EXISTS 기반 필터로 계산됩니다.</p>
     *
     * @param projectId 조회 대상 프로젝트의 ID (이 프로젝트와 연결된 데이터셋만 반환)
     * @param pageable  페이지네이션 및 정렬 정보
     * @return 각 데이터와 해당 데이터에 연결된 프로젝트 개수를 포함한 Page&lt;DataWithProjectCountDto&gt;
     */
    @Override
    public Page<DataWithProjectCountDto> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity",  "[getConnectedDataSetsAssociatedWithProject] 지정된 프로젝트에 연결된 데이터셋 목록 조회 시작. projectId=" + projectId);

        // alias path (튜플에서 꺼낼 때 사용)
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        // 서브쿼리는 SubQueryExpression
        SubQueryExpression<Long> projectCountSub =
                JPAExpressions.select(projectData.project.id.countDistinct())
                        .from(projectData)
                        .where(projectData.dataId.eq(data.id));

        // 목록 + 집계 (해당 projectId로 필터는 EXISTS로)
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        ExpressionUtils.as(projectCountSub, projectCountPath)
                )
                .from(data)
                .leftJoin(data.metadata).fetchJoin() // 1:1이면 fetchJoin OK
                .where(
                        JPAExpressions.selectOne()
                                .from(projectData)
                                .where(projectData.project.id.eq(projectId)
                                        .and(projectData.dataId.eq(data.id)))
                                .exists()
                )
                .orderBy(DataSortBuilder.fromSortOption(DataSortType.LATEST, null))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(t -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(t.get(data)),
                        t.get(projectCountPath)
                ))
                .toList();

        // total은 EXISTS로만 계산 (JOIN/그룹 불필요)
        long total = Optional.ofNullable(
                queryFactory
                        .select(data.id.count())
                        .from(data)
                        .where(
                                JPAExpressions.selectOne()
                                        .from(projectData)
                                        .where(projectData.project.id.eq(projectId)
                                                .and(projectData.dataId.eq(data.id)))
                                        .exists()
                        )
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("DataEntity",
                "[getConnectedDataSetsAssociatedWithProject] 지정된 프로젝트에 연결된 데이터셋 목록 조회 완료. projectId=" + projectId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }


    /**
     * 주어진 데이터 ID 목록에 해당하는 데이터셋과 각 데이터셋에 연결된 프로젝트 수를 조회합니다.
     *
     * @param dataIds 조회할 데이터셋의 ID 목록
     * @return 데이터셋과 연결된 프로젝트 수 정보를 담은 DataWithProjectCountDto 리스트. 입력 목록이 비어 있거나 null이면 빈 리스트를 반환합니다.
     */
    @Override
    public List<DataWithProjectCountDto> findConnectedDataSetsAssociatedWithProjectByIds(List<Long> dataIds) {
        if (dataIds == null || dataIds.isEmpty()) return List.of();

        Instant startTime = LoggerFactory.query()
                .logQueryStart("DataEntity", "[getConnectedDataSetsAssociatedWithProjectByIds] dataIds=" + dataIds);

        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");
        SubQueryExpression<Long> projectCountSub =
                JPAExpressions.select(projectData.project.id.countDistinct())
                        .from(projectData)
                        .where(projectData.dataId.eq(data.id)); // ← 이 data가 전체 몇 프로젝트에 연결됐는지

        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        ExpressionUtils.as(projectCountSub, projectCountPath)
                )
                .from(data)
                .join(data.metadata).fetchJoin() // 1:1
                .where(
                        DataFilterPredicate.dataIdIn(dataIds)
                )
                .orderBy(DataSortBuilder.fromSortOption(DataSortType.LATEST, null))
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(t -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(t.get(data)),
                        t.get(projectCountPath)
                ))
                .toList();

        LoggerFactory.query().logQueryEnd("DataEntity",
                "[getConnectedDataSetsAssociatedWithProjectByIds] 완료 dataIds=" + dataIds, startTime);
        return contents;
    }

    /**
     * 지정된 개수만큼 최신순으로 데이터셋 목록을 간단하게 조회합니다.
     *
     * @param size 조회할 데이터셋의 최대 개수
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
     * 인기도 점수에 따라 상위 데이터셋을 조회하고 각 데이터셋에 연결된 프로젝트 수를 함께 반환합니다.
     *
     * <p>각 결과는 도메인 Data와 그 데이터에 연결된 고유 프로젝트 수를 포함합니다. 결과는 계산된 인기 점수(데이터 메타·연결 프로젝트 수 기반) 내림차순으로 정렬되어 반환됩니다. 메타데이터는 함께 로드됩니다.</p>
     *
     * @param size 반환할 최대 데이터셋 개수
     * @return 도메인 Data와 연결된 프로젝트 수를 포함하는 DTO 목록 (인기도 내림차순, 최대 size)
     */
    @Override
    public List<DataWithProjectCountDto> getPopularDataSets(int size) {
        Instant startTime = LoggerFactory.query().logQueryStart(
                "DataEntity", "[searchPopularDataSets] 인기있는 데이터셋 목록 조회 시작. size=" + size);

        // 튜플에서 꺼낼 alias path
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        // 전체 프로젝트 수 = 서브쿼리(count distinct) → NumberExpression으로 래핑
        var projectCountSub = com.querydsl.jpa.JPAExpressions
                .select(projectData.project.id.countDistinct())
                .from(projectData)
                .where(projectData.dataId.eq(data.id)); // 이 data가 전체 몇 프로젝트에 연결됐는지

        // QueryDSL의 score 빌더가 NumberExpression을 요구하므로 numberTemplate로 감싼다
        NumberExpression<Long> projectCountExpr =
                Expressions.numberTemplate(Long.class, "({0})", projectCountSub);

        // 인기 점수
        NumberExpression<Double> score = DataPopularOrderBuilder.popularScore(data, projectCountExpr);

        // 메인 쿼리: 조인/그룹바이 없이 깔끔하게 (메타데이터는 1:1이므로 fetch join OK)
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        ExpressionUtils.as(projectCountExpr, projectCountPath)
                )
                .from(data)
                .join(data.metadata).fetchJoin()   // 1:1/필수면 join으로 명확히
                .orderBy(score.desc())
                .limit(size)
                .fetch();

        List<DataWithProjectCountDto> result = tuples.stream()
                .map(t -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(t.get(data)),
                        t.get(projectCountPath)
                ))
                .toList();

        LoggerFactory.query().logQueryEnd("DataEntity",
                "[searchPopularDataSets] 인기있는 데이터셋 목록 조회 완료. size=" + size, startTime);
        return result;
    }

    /**
     * 지정한 사용자가 업로드한 데이터셋 목록을 페이지 단위로 조회하고, 각 데이터셋마다 연결된 프로젝트 수를 함께 반환합니다.
     *
     * <p>조회 결과는 데이터 메타데이터를 페치 조인하여 반환하며, 삭제된 데이터는 제외합니다. 각 항목의 프로젝트 수는 데이터별로 중복을 제거한 프로젝트 수로 계산됩니다.
     *
     * @param userId 조회할 사용자 ID
     * @param pageable 결과 페이징 정보(이전 값이 null인 경우 기본값 page=0, size=5 사용)
     * @return 각 데이터셋과 해당 데이터셋에 연결된 프로젝트 수를 포함한 페이징 결과 (Page&lt;DataWithProjectCountDto&gt;)
     */
    @Override
    public Page<DataWithProjectCountDto> findUserDataSets(Long userId, Pageable pageable) {
        // 기본 Pageable: page=0, size=5
        Pageable effectivePageable = (pageable == null)
                ? PageRequest.of(0, 5)
                : pageable;

        Instant startTime = LoggerFactory.query().logQueryStart("DataEntity",  "[findUserDataSets] 회원이 업로드한 데이터셋 목록 조회 시작. userId=" + userId);

        // alias path (튜플에서 꺼낼 때 사용)
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        // 서브쿼리는 SubQueryExpression
        SubQueryExpression<Long> projectCountSub =
                JPAExpressions.select(projectData.project.id.countDistinct())
                        .from(projectData)
                        .where(projectData.dataId.eq(data.id));

        // 목록 + 집계 (해당 projectId로 필터는 EXISTS로)
        List<Tuple> tuples = queryFactory
                .select(
                        data,
                        ExpressionUtils.as(projectCountSub, projectCountPath)
                )
                .from(data)
                .leftJoin(data.metadata).fetchJoin() // 1:1이면 fetchJoin OK
                .where(
                        DataFilterPredicate.notDeleted(),
                        DataFilterPredicate.userIdEq(userId)
                )
                .orderBy(DataSortBuilder.fromSortOption(DataSortType.LATEST, null))
                .offset(effectivePageable.getOffset())
                .limit(effectivePageable.getPageSize())
                .fetch();

        List<DataWithProjectCountDto> contents = tuples.stream()
                .map(t -> new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(t.get(data)),
                        t.get(projectCountPath)
                ))
                .toList();

        // total은 EXISTS로만 계산 (JOIN/그룹 불필요)
        long total = Optional.ofNullable(
                queryFactory
                        .select(data.id.count())
                        .from(data)
                        .where(
                                DataFilterPredicate.notDeleted(),
                                DataFilterPredicate.userIdEq(userId)
                        )
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("DataEntity",
                "[findUserDataSets] 회원이 업로드한 데이터셋 목록 조회 완료. userId=" + userId, startTime);
        return new PageImpl<>(contents, effectivePageable, total);
    }
}
