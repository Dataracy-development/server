/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.web.api.datatype;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reference", description = "참조 데이터 관련 API")
@RequestMapping("/api/v1/references")
public interface DataTypeApi {
  /**
   * 모든 데이터 유형의 전체 목록을 반환하는 REST API 엔드포인트입니다.
   *
   * @return 전체 데이터 유형 정보를 포함하는 성공 응답 객체
   */
  @Operation(
      summary = "전체 데이터 유형 리스트를 조회",
      description = "DB에서 전체 데이터 유형 리스트를 조회한다.",
      security = {})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "전체 데이터 유형 리스트 조회",
            useReturnTypeSchema = true),
      })
  @GetMapping("/data-types")
  ResponseEntity<SuccessResponse<AllDataTypesWebResponse>> findAllDataTypes();
}
