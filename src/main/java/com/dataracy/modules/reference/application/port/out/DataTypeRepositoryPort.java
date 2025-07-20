package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.DataType;

import java.util.List;
import java.util.Map;
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
 * 데이터 유형의 고유 ID로 DataType 객체를 조회합니다.
 *
 * @param dataTypeId 조회할 데이터 유형의 ID
 * @return 해당 ID의 DataType이 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    Optional<DataType> findDataTypeById(Long dataTypeId);

    /**
 * 주어진 ID에 해당하는 DataType 엔티티가 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param dataTypeId 존재 여부를 확인할 DataType의 고유 ID
 * @return DataType이 존재하면 true, 존재하지 않으면 false
 */
boolean existsDataTypeById(Long dataTypeId);

    /**
 * 지정된 데이터 타입 ID에 해당하는 레이블을 반환합니다.
 *
 * @param dataTypeId 레이블을 조회할 데이터 타입의 고유 ID
 * @return 레이블이 존재하면 해당 문자열을 포함한 Optional, 존재하지 않으면 빈 Optional
 */
Optional<String> getLabelById(Long dataTypeId);

    /**
 * 주어진 데이터 타입 ID 목록에 대해 각 ID에 해당하는 라벨을 매핑하여 반환합니다.
 *
 * @param dataTypeIds 라벨을 조회할 데이터 타입 ID 목록
 * @return 각 데이터 타입 ID와 해당 라벨이 매핑된 Map 객체
 */
Map<Long, String> getLabelsByIds(List<Long> dataTypeIds);
}
