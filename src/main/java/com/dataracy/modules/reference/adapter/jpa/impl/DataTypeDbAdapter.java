package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.DataTypeEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.DataTypeJpaRepository;
import com.dataracy.modules.reference.application.port.out.DataTypePort;
import com.dataracy.modules.reference.domain.model.DataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataTypeDbAdapter implements DataTypePort {
    private final DataTypeJpaRepository dataTypeJpaRepository;

    /**
     * 모든 데이터 유형을 조회하여 도메인 객체 리스트로 반환합니다.
     *
     * @return 데이터 유형 도메인 객체의 리스트
     */
    @Override
    public List<DataType> findAllDataTypes() {
        Instant startTime = LoggerFactory.db().logQueryStart("DataTypeEntity", "[findAll] 데이터 유형 목록 조회 시작");
        List<DataTypeEntity> dataTypeEntities = dataTypeJpaRepository.findAll();
        List<DataType> dataTypes = dataTypeEntities.stream()
                .map(DataTypeEntityMapper::toDomain)
                .toList();
        LoggerFactory.db().logQueryEnd("DataTypeEntity", "[findAll] 데이터 유형 목록 조회 종료", startTime);
        return dataTypes;
    }

    /**
     * 주어진 ID에 해당하는 데이터 유형 도메인 객체를 Optional로 반환한다.
     *
     * ID가 null이거나 해당 데이터 유형이 존재하지 않으면 빈 Optional을 반환한다.
     *
     * @param dataTypeId 조회할 데이터 유형의 ID
     * @return 데이터 유형 도메인 객체의 Optional
     */
    @Override
    public Optional<DataType> findDataTypeById(Long dataTypeId) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataTypeEntity", "[findById] 데이터 유형 목록 조회 시작 dataTypeId=" + dataTypeId);
        if (dataTypeId == null) {
            return Optional.empty();
        }
        Optional<DataType> dataType = dataTypeJpaRepository.findById(dataTypeId)
                .map(DataTypeEntityMapper::toDomain);
        LoggerFactory.db().logQueryEnd("DataTypeEntity", "[findById] 데이터 유형 목록 조회 종료 dataTypeId=" + dataTypeId, startTime);
        return dataType;
    }

    /**
     * 주어진 ID에 해당하는 데이터 타입이 데이터베이스에 존재하는지 확인합니다.
     *
     * @param dataTypeId 존재 여부를 확인할 데이터 타입의 ID
     * @return 데이터 타입이 존재하면 true, ID가 null이거나 존재하지 않으면 false
     */
    @Override
    public boolean existsDataTypeById(Long dataTypeId) {
        if (dataTypeId == null) {
            return false;
        }
        boolean isExists = dataTypeJpaRepository.existsById(dataTypeId);
        LoggerFactory.db().logExist("DataTypeEntity", "[existsById] 데이터 유형 존재 유무 확인 dataTypeId=" + dataTypeId + ", isExists=" + isExists);
        return isExists;
    }

    /**
     * 주어진 ID에 해당하는 데이터 타입의 라벨을 Optional로 반환합니다.
     *
     * @param dataTypeId 조회할 데이터 타입의 ID
     * @return 데이터 타입이 존재하면 라벨을 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    @Override
    public Optional<String> getLabelById(Long dataTypeId) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataTypeEntity", "[findLabelById] 데이터 유형 라벨 조회 시작 dataTypeId=" + dataTypeId);
        if (dataTypeId == null) {
            return Optional.empty();
        }
        Optional<String> label = dataTypeJpaRepository.findLabelById(dataTypeId);
        LoggerFactory.db().logQueryEnd("DataTypeEntity", "[findLabelById] 데이터 유형 라벨 조회 종료 dataTypeId=" + dataTypeId + ", label=" + label, startTime);
        return label;
    }

    /**
     * 주어진 데이터 타입 ID 목록에 대해 각 ID에 해당하는 라벨을 맵 형태로 반환합니다.
     *
     * @param dataTypeIds 라벨을 조회할 데이터 타입 ID 목록
     * @return 각 데이터 타입 ID와 해당 라벨의 매핑 맵
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> dataTypeIds) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataTypeEntity", "[findAllById] 데이터 유형 라벨 목록 조회 시작");
        Map<Long, String> labels = dataTypeJpaRepository.findAllById(dataTypeIds)
                .stream()
                .collect(Collectors.toMap(DataTypeEntity::getId, DataTypeEntity::getLabel));
        LoggerFactory.db().logQueryEnd("DataTypeEntity", "[findAllById] 데이터 유형 라벨 목록 조회 종료", startTime);
        return labels;
    }
}
