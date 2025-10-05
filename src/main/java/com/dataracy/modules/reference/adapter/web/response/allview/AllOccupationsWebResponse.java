package com.dataracy.modules.reference.adapter.web.response.allview;

import java.util.List;

import com.dataracy.modules.reference.adapter.web.response.singleview.OccupationWebResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "직업 리스트 조회 응답")
public record AllOccupationsWebResponse(List<OccupationWebResponse> occupations) {}
