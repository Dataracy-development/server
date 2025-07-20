package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.DataSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.DataSourceEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.DataSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.DataSourceRepositoryPort;
import com.dataracy.modules.reference.domain.model.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<DataSource> findAllDataSources() {
        List<DataSourceEntity> dataSourceEntities = dataSourceJpaRepository.findAll();
        return dataSourceEntities.stream()
                .map(DataSourceEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 ID로 데이터 출처를 조회하여 도메인 객체로 변환한 Optional로 반환한다.
     *
     * @param dataSourceId 조회할 데이터 출처의 ID. null일 경우 빈 Optional을 반환한다.
     * @return 데이터 출처 도메인 객체의 Optional. 해당 ID가 없거나 null이면 빈 Optional을 반환한다.
     */
    @Override
    public Optional<DataSource> findDataSourceById(Long dataSourceId) {
        if (dataSourceId == null) {
            return Optional.empty();
        }
        return dataSourceJpaRepository.findById(dataSourceId)
                .map(DataSourceEntityMapper::toDomain);
    }

    /**
     * 주어진 ID에 해당하는 데이터 소스의 존재 여부를 반환합니다.
     *
     * @param dataSourceId 데이터 소스의 ID
     * @return 데이터 소스가 존재하면 true, 존재하지 않으면 false입니다. ID가 null인 경우에도 false를 반환합니다.
     */
    @Override
    public boolean existsDataSourceById(Long dataSourceId) {
        if (dataSourceId == null) {
            return false;
        }
        return dataSourceJpaRepository.existsById(dataSourceId);
    }

    /**
     * 주어진 ID에 해당하는 데이터 소스의 라벨을 Optional로 반환합니다.
     *
     * @param dataSourceId 조회할 데이터 소스의 ID
     * @return 데이터 소스가 존재하면 라벨을 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<String> getLabelById(Long dataSourceId) {
        if (dataSourceId == null) {
            return Optional.empty();
        }
        return dataSourceJpaRepository.findLabelById(dataSourceId);
    }

    /**
     * 주어진 데이터 소스 ID 목록에 해당하는 데이터 소스의 ID와 라벨을 매핑한 Map을 반환합니다.
     *
     * @param dataSourceIds 조회할 데이터 소스의 ID 목록
     * @return 각 데이터 소스 ID와 해당 라벨이 매핑된 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> dataSourceIds) {
        return dataSourceJpaRepository.findAllById(dataSourceIds)
                .stream()
                .collect(Collectors.toMap(DataSourceEntity::getId, DataSourceEntity::getLabel));
    }
}
