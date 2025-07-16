package com.dataracy.modules.reference.adapter.persistence.impl;

import com.dataracy.modules.reference.adapter.persistence.entity.DataTypeEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.DataTypeEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.DataTypeJpaRepository;
import com.dataracy.modules.reference.application.port.out.DataTypeRepositoryPort;
import com.dataracy.modules.reference.domain.model.DataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataTypeRepositoryAdapter implements DataTypeRepositoryPort {
    private final DataTypeJpaRepository dataTypeJpaRepository;

    /**
     * 모든 데이터 유형 엔티티를 조회하여 도메인 객체 리스트로 반환합니다.
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
     * 주어진 ID에 해당하는 데이터 유형를 조회하여 Optional로 반환한다.
     *
     * @param dataTypeId 조회할 데이터 유형의 ID
     * @return 데이터 유형 도메인 객체의 Optional. 해당 ID가 없거나 null인 경우 빈 Optional을 반환한다.
     */
    @Override
    public Optional<DataType> findDataTypeById(Long dataTypeId) {
        return dataTypeJpaRepository.findById(dataTypeId)
                .map(DataTypeEntityMapper::toDomain);
    }

    @Override
    public Boolean existsDataTypeById(Long dataTypeId) {
        return dataTypeJpaRepository.existsById(dataTypeId);
    }
}
