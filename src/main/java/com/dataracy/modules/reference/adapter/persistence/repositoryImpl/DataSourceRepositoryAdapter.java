package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

import com.dataracy.modules.reference.adapter.persistence.entity.DataSourceEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.DataSourceEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.DataSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.DataSourceRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataSource;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryAdapter implements DataSourceRepositoryPort {
    private final DataSourceJpaRepository dataSourceJpaRepository;

    /**
     * dataSource 엔티티의 모든 데이터셋을 조회한다.
     * @return dataSource 데이터셋
     */
    @Override
    public List<DataSource> allDataSources() {
        List<DataSourceEntity> dataSourceEntities = dataSourceJpaRepository.findAll();
        return dataSourceEntities.stream()
                .map(DataSourceEntityMapper::toDomain)
                .toList();
    }

    /**
     * 데이터 출처 id에 해당하는 데이터 출처가 존재하면 조회한다.
     * @param dataSourceId 작성자 유형 아이디
     * @return 데이터 출처
     */
    @Override
    public DataSource findDataSourceById(Long dataSourceId) {
        if (dataSourceId == null) {
            return null;
        }

        DataSourceEntity dataSourceEntity = dataSourceJpaRepository.findById(dataSourceId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE));
        return DataSourceEntityMapper.toDomain(dataSourceEntity);
    }
}
