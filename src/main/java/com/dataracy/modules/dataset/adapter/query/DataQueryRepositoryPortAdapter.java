package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.dataset.adapter.query.sort.DataPopularOrderBuilder;
import com.dataracy.modules.dataset.application.dto.response.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.query.DataQueryRepositoryPort;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class DataQueryRepositoryPortAdapter implements DataQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QDataEntity data = QDataEntity.dataEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 데이터 ID로 데이터를 조회하여 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 식별자
     * @return 데이터가 존재하면 도메인 객체를, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<Data> findDataById(Long dataId) {
        DataEntity entity = queryFactory
                .selectFrom(data)
                .where(DataFilterPredicate.dataIdEq(dataId))
                .fetchOne();

        return Optional.ofNullable(DataEntityMapper.toDomain(entity));
    }

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
}
