/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.api.read;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.dataset.adapter.web.response.read.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@Tag(name = "Data - Read", description = "데이터셋 관련 API - 조회")
@RequestMapping("/api/v1/datasets")
public interface DataReadApi {
  /**
   * 다운로드 수와 연결된 프로젝트 수를 기준으로 인기 있는 데이터셋 목록을 조회합니다.
   *
   * @param size 반환할 데이터셋의 최대 개수 (1 이상)
   * @return 인기 데이터셋 목록이 포함된 성공 응답
   */
  @Operation(summary = "인기있는 데이터셋을 조회한다.", description = "다운로드가 많은, 연결된 프로젝트 개수가 많은 데이터셋을 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "인기 있는 데이터셋 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/popular")
  ResponseEntity<SuccessResponse<List<PopularDataWebResponse>>> getPopularDataSets(
      @RequestParam(name = "size") @Min(1) int size);

  /**
   * 지정된 데이터셋 ID에 해당하는 데이터셋의 상세 정보를 조회합니다.
   *
   * @param dataId 조회할 데이터셋의 고유 식별자
   * @return 데이터셋의 상세 정보를 담은 성공 응답 객체
   */
  @Operation(summary = "데이터셋 세부정보를 조회한다.", description = "데이터셋 세부정보를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "데이터셋 세부정보 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/{dataId}")
  ResponseEntity<SuccessResponse<DataDetailWebResponse>> getDataDetail(
      @PathVariable(name = "dataId") Long dataId);

  /**
   * 최근에 추가된 데이터셋의 최소 정보 목록을 반환합니다.
   *
   * @param size 반환할 데이터셋의 개수 (1 이상)
   * @return 최근 추가된 데이터셋의 최소 정보 목록이 포함된 성공 응답
   */
  @Operation(summary = "간단한 최신 데이터셋 목록을 조회한다.", description = "최신 데이터셋 목록을 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "간단한 최신 데이터셋 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/recent")
  ResponseEntity<SuccessResponse<List<RecentMinimalDataWebResponse>>> getRecentDataSets(
      @RequestParam(name = "size") @Min(1) int size);

  /**
   * 데이터셋을 카테고리(토픽)별로 그룹화하여 각 카테고리별 데이터셋 개수를 반환합니다.
   *
   * @return 각 카테고리별 데이터셋 개수 목록이 포함된 성공 응답
   */
  @Operation(summary = "카테고리별 데이터셋 개수를 카운트한다.", description = "카테고리별 데이터셋 개수를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "카테고리별 데이터셋 개수를 조회한다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/group-by/topic")
  ResponseEntity<SuccessResponse<List<DataGroupCountWebResponse>>> getDataCountByTopicLabel();

  /**
   * 지정한 프로젝트에 연결된 데이터셋 목록을 페이지네이션하여 반환합니다.
   *
   * @param projectId 데이터셋을 조회할 프로젝트의 고유 ID (1 이상)
   * @param pageable 페이지네이션 정보 (기본 페이지 크기 3, 0페이지부터 시작)
   * @return 프로젝트에 연결된 데이터셋 목록이 포함된 성공 응답 객체
   */
  @Operation(summary = "프로젝트와 연결된 데이터셋 리스트를 조회한다.", description = "프로젝트와 연결된 데이터셋 리스트를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "프로젝트와 연결된 데이터셋 리스트를 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/connected-to-project")
  ResponseEntity<SuccessResponse<Page<ConnectedDataWebResponse>>>
      findConnectedDataSetsAssociatedWithProject(
          @RequestParam @Min(1) Long projectId,
          @PageableDefault(size = 3, page = 0) Pageable pageable);

  /**
   * 로그인한 회원이 업로드한 데이터셋의 페이징된 목록을 조회합니다.
   *
   * <p>인증된 사용자의 업로드 데이터셋을 페이지 단위로 반환합니다. 해당 엔드포인트는 인증(Authorization 헤더, Bearer 토큰)을 필요로 합니다.
   *
   * @param userId 현재 인증된 회원의 ID (컨트롤러에서 주입됨)
   * @param pageable 페이지 정보 (기본 페이지 크기: 5)
   * @return 인증된 회원이 업로드한 데이터셋을 담은 SuccessResponse를 포함한 ResponseEntity (Page<UserDataWebResponse>)
   */
  @Operation(
      summary = "로그인한 회원이 업로드한 데이터셋 리스트를 조회한다.",
      description = "로그인한 회원이 업로드한 데이터셋 리스트를 조회한다.")
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
            description = "로그인한 회원이 업로드한 데이터셋 리스트를 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/me")
  ResponseEntity<SuccessResponse<Page<UserDataWebResponse>>> findUserDataSets(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @PageableDefault(size = 5, page = 0) Pageable pageable);
}
