package com.dataracy.modules.reference.application.port.in.datatype;

import java.util.List;
import java.util.Map;

public interface GetDataTypeLabelFromIdUseCase {
    /**
 * 주어진 데이터 타입 ID에 해당하는 라벨을 반환합니다.
 *
 * @param dataTypeId 데이터 타입의 고유 식별자
 * @return 해당 데이터 타입의 라벨 문자열
 */
String getLabelById(Long dataTypeId);

    /**
 * 여러 데이터 타입 ID에 대해 각 ID에 해당하는 레이블을 반환합니다.
 *
 * @param dataTypeIds 조회할 데이터 타입 ID 목록
 * @return 각 데이터 타입 ID와 해당 레이블이 매핑된 Map 객체
 */
Map<Long, String> getLabelsByIds(List<Long> dataTypeIds);
}
