package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.DataTypeEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.DataTypeJpaRepository;
import com.dataracy.modules.reference.application.port.out.DataTypeRepositoryPort;
import com.dataracy.modules.reference.domain.model.DataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataTypeRepositoryAdapter implements DataTypeRepositoryPort {
    private final DataTypeJpaRepository dataTypeJpaRepository;

    /**
     * 모든 데이터 유형을 조회하여 도메인 객체 리스트로 반환합니다.
     *
     * @return 데이터 유형 도메인 객체의 리스트
     */
    @Override
    public List<DataType> findAllDataTypes() {
        List<DataTypeEntity> dataTypeEntities = dataTypeJpaRepository.findAll();
        return dataTypeEntities.stream()
                .map(DataTypeEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 ID로 데이터 유형을 조회하여 Optional로 반환한다.
     *
     * @param dataTypeId 조회할 데이터 유형의 ID
     * @return 데이터 유형 도메인 객체의 Optional. ID가 없거나 null인 경우 빈 Optional을 반환한다.
     */
    @Override
    public Optional<DataType> findDataTypeById(Long dataTypeId) {
        if (dataTypeId == null) {
            return Optional.empty();
        }
        return dataTypeJpaRepository.findById(dataTypeId)
                .map(DataTypeEntityMapper::toDomain);
    }

    /**
     * 지정한 ID의 데이터 타입이 데이터베이스에 존재하는지 확인합니다.
     *
     * @param dataTypeId 존재 여부를 확인할 데이터 타입의 ID
     * @return 데이터 타입이 존재하면 true, 존재하지 않으면 false
     */
    @Override
    public boolean existsDataTypeById(Long dataTypeId) {
        if (dataTypeId == null) {
            return false;
        }
        return dataTypeJpaRepository.existsById(dataTypeId);
    }

    /**
     * 주어진 ID에 해당하는 데이터 타입의 라벨을 조회합니다.
     *
     * @param dataTypeId 조회할 데이터 타입의 ID
     * @return 데이터 타입이 존재하면 라벨을 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<String> getLabelById(Long dataTypeId) {
        if (dataTypeId == null) {
            return Optional.empty();
        }
        return dataTypeJpaRepository.findById(dataTypeId)
                .map(DataTypeEntity::getLabel);
    }

    @Override
    public Map<Long, String> getLabelsByIds(List<Long> dataTypeIds) {
        return dataTypeJpaRepository.findAllById(dataTypeIds)
                .stream()
                .collect(Collectors.toMap(DataTypeEntity::getId, DataTypeEntity::getLabel));
    }
}
