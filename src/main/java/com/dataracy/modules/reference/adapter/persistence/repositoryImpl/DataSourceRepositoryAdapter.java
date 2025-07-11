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
     * 모든 데이터 소스 엔티티를 조회하여 도메인 객체 리스트로 반환합니다.
     *
     * @return 데이터 소스 도메인 객체의 리스트
     */
    @Override
    public List<DataSource> allDataSources() {
        List<DataSourceEntity> dataSourceEntities = dataSourceJpaRepository.findAll();
        return dataSourceEntities.stream()
                .map(DataSourceEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 데이터 출처 ID로 데이터 출처를 조회한다.
     *
     * @param dataSourceId 조회할 데이터 출처의 ID
     * @return 데이터 출처 도메인 객체. ID가 null이면 null을 반환한다.
     * @throws ReferenceException 데이터 출처를 찾을 수 없는 경우 발생
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
