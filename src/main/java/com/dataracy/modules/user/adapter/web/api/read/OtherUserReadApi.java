package com.dataracy.modules.user.adapter.web.api.read;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User - Read", description = "사용자 관련 API - 조회")
@RequestMapping("/api/v1/users")
public interface OtherUserReadApi {
  /**
   * 타인 유저의 회원 정보를 조회한다.
   *
   * @param userId 조회할 대상 유저의 식별자
   * @return 조회한 회원 정보를 담은 SuccessResponse를 포함한 ResponseEntity
   */
  @Operation(summary = "타인 유저의 회원 정보를 조회한다.", description = "타인 유저의 회원 정보를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "타인의 회원 정보 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/{userId}")
  ResponseEntity<SuccessResponse<GetOtherUserInfoWebResponse>> getOtherUserInfo(
      @PathVariable Long userId);

  /**
   * 특정 사용자가 업로드한 프로젝트 목록의 지정 페이지를 조회한다.
   *
   * <p>반환값은 요청한 사용자의 프로젝트들을 페이지 형태로 담은 SuccessResponse를 ResponseEntity로 감싼 형태이다.
   *
   * @param userId 조회 대상 사용자의 식별자(예: 123)
   * @param pageable 페이지 요청 정보(기본: page=0, size=5). 페이지 번호는 0부터 시작한다.
   * @return 요청한 페이지의 프로젝트 정보들을 담은 SuccessResponse<Page<GetOtherUserProjectWebResponse>>를 포함한
   *     ResponseEntity
   */
  @Operation(
      summary = "타인 유저가 업로드한 프로젝트 목록의 추가 해당 페이지를 조회한다.",
      description = "타인 유저가 업로드한 프로젝트 목록의 추가 해당 페이지를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "타인 유저가 업로드한 프로젝트 목록의 추가 해당 페이지를 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/{userId}/projects")
  ResponseEntity<SuccessResponse<Page<GetOtherUserProjectWebResponse>>> getOtherProjects(
      @PathVariable Long userId, @PageableDefault(size = 5, page = 0) Pageable pageable);

  /**
   * 타인 유저가 업로드한 데이터셋 목록의 특정 페이지를 조회한다.
   *
   * <p>상세: 대상 유저(userId)가 업로드한 데이터셋들을 페이징된 형태로 반환한다.
   *
   * @param userId 조회 대상 유저의 식별자
   * @param pageable 페이지 번호, 페이지 크기 등 페이징 정보 (기본: size=5, page=0)
   * @return 요청한 페이지의 데이터셋 목록을 담은 SuccessResponse를 포함하는 ResponseEntity
   */
  @Operation(
      summary = "타인 유저가 업로드한 데이터셋 목록의 추가 해당 페이지를 조회한다.",
      description = "타인 유저가 업로드한 데이터셋 목록의 추가 해당 페이지를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "타인 유저가 업로드한 데이터셋 목록의 추가 해당 페이지를 조회에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/{userId}/datasets")
  ResponseEntity<SuccessResponse<Page<GetOtherUserDataWebResponse>>> getOtherDataSets(
      @PathVariable Long userId, @PageableDefault(size = 5, page = 0) Pageable pageable);
}
