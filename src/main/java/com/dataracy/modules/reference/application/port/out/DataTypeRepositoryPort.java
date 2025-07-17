package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.DataType;

import java.util.List;
import java.util.Optional;

/**
 * DataType db에 접근하는 포트
 */
public interface DataTypeRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 데이터 유형의 리스트를 반환합니다.
 *
 * @return 데이터 유형 객체들의 리스트
 */
    List<DataType> findAllDataTypes();

    /**
 * 주어진 ID에 해당하는 DataType 객체를 조회합니다.
 *
 * @param dataTypeId 조회할 데이터 유형의 고유 식별자
 * @return 해당 ID의 DataType가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    Optional<DataType> findDataTypeById(Long dataTypeId);

    boolean existsDataTypeById(Long dataTypeId);
}
