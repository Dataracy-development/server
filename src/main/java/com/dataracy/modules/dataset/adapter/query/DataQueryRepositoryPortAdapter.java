package com.dataracy.modules.dataset.adapter.query;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.dataset.application.port.query.DataQueryRepositoryPort;
import com.dataracy.modules.dataset.domain.model.Data;
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

    /**
     * 주어진 데이터 ID에 해당하는 데이터를 조회하여 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 식별자
     * @return 데이터가 존재하면 해당 도메인 객체를, 없으면 빈 Optional을 반환합니다.
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
    public List<Data> findPopularDataSets(int size) {
        return List.of();
    }
}
