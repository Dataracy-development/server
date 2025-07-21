package com.dataracy.modules.data.adapter.query;

import com.dataracy.modules.data.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.data.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.data.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.data.adapter.query.predicates.DataFilterPredicate;
import com.dataracy.modules.data.application.port.query.DataQueryRepositoryPort;
import com.dataracy.modules.data.domain.model.Data;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class DataQueryRepositoryPortAdapter implements DataQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QDataEntity data = QDataEntity.dataEntity;

    @Override
    public Optional<Data> findDataById(Long dataId) {
        DataEntity entity = queryFactory
                .selectFrom(data)
                .where(DataFilterPredicate.dataIdEq(dataId))
                .fetchOne();

        return Optional.ofNullable(DataEntityMapper.toDomain(entity));
    }
}
