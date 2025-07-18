package com.dataracy.modules.reference.application.port.in.datatype;

public interface GetDataTypeLabelFromIdUseCase {
    /**
 * 주어진 데이터 타입 ID에 해당하는 라벨을 반환합니다.
 *
 * @param dataTypeId 데이터 타입의 고유 식별자
 * @return 해당 데이터 타입의 라벨 문자열
 */
String getLabelById(Long dataTypeId);
}
