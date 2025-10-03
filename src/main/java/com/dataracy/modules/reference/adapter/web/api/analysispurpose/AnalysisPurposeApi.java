/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.web.api.analysispurpose;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAnalysisPurposesWebResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reference", description = "참조 데이터 관련 API")
@RequestMapping("/api/v1/references")
public interface AnalysisPurposeApi {
  /**
   * 전체 분석 목적 목록을 조회하여 반환한다. 데이터베이스에 저장된 모든 분석 목적 정보를 조회하여 성공 응답 형태로 반환한다.
   *
   * @return 전체 분석 목적 목록이 포함된 성공 응답 객체
   */
  @Operation(
      summary = "전체 분석 목적 리스트를 조회",
      description = "DB에서 전체 분석 목적 리스트를 조회한다.",
      security = {})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "전체 분석 목적 리스트 조회",
            useReturnTypeSchema = true),
      })
  @GetMapping("/analysis-purposes")
  ResponseEntity<SuccessResponse<AllAnalysisPurposesWebResponse>> findAllAnalysisPurposes();
}
