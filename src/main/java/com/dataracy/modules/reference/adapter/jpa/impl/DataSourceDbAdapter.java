package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.DataSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.DataSourceEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.DataSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.DataSourcePort;
import com.dataracy.modules.reference.domain.model.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataSourceDbAdapter implements DataSourcePort {
    private final DataSourceJpaRepository dataSourceJpaRepository;

    /**
     * 모든 데이터 소스 엔티티를 조회하여 도메인 객체 리스트로 반환합니다.
     *
     * @return 데이터 소스 도메인 객체의 리스트
     */
    @Override
    public List<DataSource> findAllDataSources() {
        Instant startTime = LoggerFactory.db().logQueryStart("DataSourceEntity", "[findAll] 데이터 출처 목록 조회 시작");

        List<DataSourceEntity> dataSourceEntities = dataSourceJpaRepository.findAll();
        List<DataSource> dataSources = dataSourceEntities.stream()
                .map(DataSourceEntityMapper::toDomain)
                .toList();

        LoggerFactory.db().logQueryEnd("DataSourceEntity", "[findAll] 데이터 출처 목록 조회 종료", startTime);
        return dataSources;
    }

    /**
     * 주어진 ID에 해당하는 데이터 출처를 조회하여 도메인 객체로 반환한다.
     *
     * @param dataSourceId 조회할 데이터 출처의 ID. null이면 빈 Optional을 반환한다.
     * @return 데이터 출처 도메인 객체의 Optional. 해당 ID가 없거나 null이면 빈 Optional을 반환한다.
     */
    @Override
    public Optional<DataSource> findDataSourceById(Long dataSourceId) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataSourceEntity", "[findById] 데이터 출처 목록 조회 시작 dataSourceId=" + dataSourceId);

        if (dataSourceId == null) {
            return Optional.empty();
        }
        Optional<DataSource> dataSource = dataSourceJpaRepository.findById(dataSourceId)
                .map(DataSourceEntityMapper::toDomain);

        LoggerFactory.db().logQueryEnd("DataSourceEntity", "[findById] 데이터 출처 목록 조회 종료 dataSourceId=" + dataSourceId, startTime);
        return dataSource;
    }

    /**
     * 주어진 ID에 해당하는 데이터 소스가 존재하는지 확인합니다.
     *
     * @param dataSourceId 확인할 데이터 소스의 ID
     * @return 데이터 소스가 존재하면 true, 존재하지 않거나 ID가 null이면 false를 반환합니다.
     */
    @Override
    public boolean existsDataSourceById(Long dataSourceId) {
        if (dataSourceId == null) {
            return false;
        }
        boolean isExists = dataSourceJpaRepository.existsById(dataSourceId);
        LoggerFactory.db().logExist("DataSourceEntity", "[existsById] 데이터 출처 존재 유무 확인 dataSourceId=" + dataSourceId + ", isExists=" + isExists);
        return isExists;
    }

    /**
     * 주어진 ID에 해당하는 데이터 소스의 라벨을 Optional로 반환합니다.
     *
     * @param dataSourceId 조회할 데이터 소스의 ID
     * @return 데이터 소스가 존재하면 해당 라벨을 포함한 Optional, 존재하지 않거나 ID가 null이면 빈 Optional
     */
    @Override
    public Optional<String> getLabelById(Long dataSourceId) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataSourceEntity", "[findLabelById] 데이터 출처 라벨 조회 시작 dataSourceId=" + dataSourceId);
        if (dataSourceId == null) {
            return Optional.empty();
        }
        Optional<String> label = dataSourceJpaRepository.findLabelById(dataSourceId);
        LoggerFactory.db().logQueryEnd("DataSourceEntity", "[findLabelById] 데이터 출처 라벨 조회 종료 dataSourceId=" + dataSourceId + ", label=" + label, startTime);
        return label;
    }

    /**
     * 주어진 데이터 소스 ID 목록에 해당하는 데이터 소스의 ID와 라벨을 매핑한 Map을 반환합니다.
     *
     * @param dataSourceIds 조회할 데이터 소스의 ID 목록
     * @return 각 데이터 소스 ID와 해당 라벨이 매핑된 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> dataSourceIds) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataSourceEntity", "[findAllById] 데이터 출처 라벨 목록 조회 시작");
        Map<Long, String> labels = dataSourceJpaRepository.findAllById(dataSourceIds)
                .stream()
                .collect(Collectors.toMap(DataSourceEntity::getId, DataSourceEntity::getLabel));
        LoggerFactory.db().logQueryEnd("DataSourceEntity", "[findAllById] 데이터 출처 라벨 목록 조회 종료", startTime);
        return labels;
    }
}
