package com.dataracy.modules.reference.application.port.in.datatype;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;

/**
 * 전체 DataType 조회 유스케이스
 */
public interface FindAllDataTypesUseCase {
    /**
 * 모든 데이터 유형을 조회하여 전체 목록을 반환합니다.
 *
 * @return 모든 데이터 소스 정보를 포함하는 AllDataTypesResponse 객체
 */
    AllDataTypesResponse findAllDataTypes();
}
