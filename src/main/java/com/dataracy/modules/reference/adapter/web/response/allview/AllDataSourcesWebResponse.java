package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.DataSourceWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * dataSource 리스트 조회를 위한 웹응답 DTO
 * @param dataSources dataSource 리스트
 */
@Schema(description = "데이터 출처 리스트 조회 응답")
public record AllDataSourcesWebResponse(List<DataSourceWebResponse> dataSources) {
}
