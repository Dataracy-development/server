package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.DataSourceWebResponse;

import java.util.List;

/**
 * dataSource 리스트 조회를 위한 웹응답 DTO
 * @param dataSources dataSource 리스트
 */
public record AllDataSourcesWebResponse(List<DataSourceWebResponse> dataSources) {
}
