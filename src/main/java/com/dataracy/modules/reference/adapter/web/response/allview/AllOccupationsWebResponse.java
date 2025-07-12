package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.OccupationWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * occupation 리스트 조회를 위한 웹응답 DTO
 * @param occupations occupation 리스트
 */
@Schema(description = "직업 리스트 조회 응답")
public record AllOccupationsWebResponse(List<OccupationWebResponse> occupations) {
}
