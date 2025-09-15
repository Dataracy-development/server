package com.dataracy.modules.reference.application.port.in.datatype;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;

public interface FindAllDataTypesUseCase {
    /**
     * 시스템에 등록된 모든 데이터 유형의 목록을 조회합니다.
     *
     * @return 전체 데이터 유형 정보를 담은 AllDataTypesResponse 객체
     */
    AllDataTypesResponse findAllDataTypes();
}
