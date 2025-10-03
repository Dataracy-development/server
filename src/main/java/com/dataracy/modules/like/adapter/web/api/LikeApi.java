/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.adapter.web.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Like", description = "좋아요 관련 API")
@RequestMapping("/api/v1/likes")
public interface LikeApi {
  /**
   * 지정된 타겟에 대해 현재 사용자의 좋아요 상태를 변경합니다.
   *
   * @param webRequest 좋아요를 적용할 타겟 정보가 포함된 요청 객체
   * @return 성공적으로 처리된 경우 성공 응답 객체를 반환합니다.
   */
  @Operation(summary = "해당 타겟에 대한 좋아요 처리를 한다.", description = "해당 타겟에 대한 좋아요 처리를 한다.")
  @Parameter(
      in = ParameterIn.HEADER,
      name = "Authorization",
      required = true,
      schema = @Schema(type = "string"),
      description = "Bearer [Access 토큰]")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "해당 타겟에 대한 좋아요 처리를 한다.",
            useReturnTypeSchema = true)
      })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<SuccessResponse<Void>> modifyTargetLike(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "좋아요 타겟 정보",
              content = @Content(schema = @Schema(implementation = TargetLikeWebRequest.class)))
          @Validated
          @RequestBody
          TargetLikeWebRequest webRequest);
}
