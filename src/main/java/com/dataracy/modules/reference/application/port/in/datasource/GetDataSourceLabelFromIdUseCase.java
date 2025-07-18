package com.dataracy.modules.reference.application.port.in.datasource;

public interface GetDataSourceLabelFromIdUseCase {
    /**
 * 주어진 데이터 소스 ID에 해당하는 라벨을 반환합니다.
 *
 * @param dataSourceId 데이터 소스의 고유 식별자
 * @return 해당 데이터 소스의 라벨 문자열
 */
String getLabelById(Long dataSourceId);
}
