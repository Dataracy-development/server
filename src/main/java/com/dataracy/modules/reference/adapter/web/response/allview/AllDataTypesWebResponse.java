package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 데이터 유형 리스트 조회를 위한 웹응답 DTO
 * @param dataTypes 데이터 유형 리스트
 */
@Schema(description = "데이터 유형 리스트 조회 응답")
public record AllDataTypesWebResponse(List<DataTypeWebResponse> dataTypes) {
}
