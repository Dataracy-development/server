package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 데이터 유형 웹 DTO와 데이터 유형 도메인 DTO를 변환하는 매퍼
 */
@Component
public class DataTypeWebMapper {
    /**
     * 도메인 데이터 유형 응답 DTO를 웹 응답 DTO로 변환합니다.
     *
     * @param dataTypeResponse 변환할 도메인 데이터 유형 응답 DTO
     * @return 변환된 데이터 유형 웹 응답 DTO
     */
    public DataTypeWebResponse toWebDto(DataTypeResponse dataTypeResponse) {
        return new DataTypeWebResponse(
                dataTypeResponse.id(),
                dataTypeResponse.value(),
                dataTypeResponse.label()
        );
    }

    // 전체 데이터 유형 리스트 조회 도메인 응답 DTO -> 전체 데이터 유형 리스트 조회 웹 응답 DTO
    public AllDataTypesWebResponse toWebDto(AllDataTypesResponse allDataTypesResponse) {
        if (allDataTypesResponse == null || allDataTypesResponse.dataTypes() == null) {
            return new AllDataTypesWebResponse(List.of());
        }

        return new AllDataTypesWebResponse(
                allDataTypesResponse.dataTypes()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
