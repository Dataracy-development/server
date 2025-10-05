package com.dataracy.modules.reference.adapter.web.response.allview;

import java.util.List;

import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "데이터 유형 리스트 조회 응답")
public record AllDataTypesWebResponse(List<DataTypeWebResponse> dataTypes) {}
