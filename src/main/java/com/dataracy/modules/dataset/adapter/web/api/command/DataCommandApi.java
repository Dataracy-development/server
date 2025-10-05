package com.dataracy.modules.dataset.adapter.web.api.command;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.command.UploadDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.download.GetDataPreSignedUrlWebResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

@Tag(name = "Data - Command", description = "데이터셋 관련 API - 생성, 수정, 삭제 등")
@RequestMapping("/api/v1/datasets")
public interface DataCommandApi {
  /**
   * 새 데이터셋 파일(및 선택적 썸네일)과 메타데이터를 업로드하여 데이터셋 레코드를 생성하고 업로드 결과를 반환합니다.
   *
   * <p>업로드된 파일과 webRequest의 정보를 바탕으로 DB에 데이터셋 정보를 저장하고, 생성된 데이터셋의 식별 정보 등을 포함한
   * UploadDataWebResponse를 SuccessResponse로 감싸서 반환합니다. 요청은 multipart/form-data 형식을 사용합니다.
   *
   * @return 생성된 데이터셋 정보를 담은 SuccessResponse<UploadDataWebResponse> (성공 시 HTTP 201)
   */
  @Operation(summary = "데이터셋을 업로드한다.", description = "제공받은 웹 요청 DTO의 데이터셋 정보를 통해 데이터셋 정보를 db에 저장한다")
  @Parameter(
      in = ParameterIn.HEADER,
      name = "Authorization",
      required = true,
      schema = @Schema(type = "string"),
      description = "Bearer [Access 토큰]")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "데이터셋 업로드에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<SuccessResponse<UploadDataWebResponse>> uploadData(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @RequestPart(value = "dataFile") MultipartFile dataFile,
      @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
      @RequestPart @Validated UploadDataWebRequest webRequest);

  /**
   * 지정한 데이터셋 ID에 해당하는 데이터셋을 새로운 파일, 썸네일, 메타데이터로 수정합니다.
   *
   * @param dataId 수정할 데이터셋의 고유 ID (1 이상)
   * @param dataFile 새로 업로드할 데이터셋 파일
   * @param thumbnailFile 새로 업로드할 썸네일 파일 (선택 사항)
   * @param webRequest 데이터셋 수정 요청 정보
   * @return 수정이 성공하면 성공 응답을 반환합니다.
   */
  @Operation(summary = "데이터셋 수정한다.", description = "제공받은 웹 요청 DTO의 데이터셋 정보를 통해 기존 데이터셋을 수정한다.")
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
            description = "데이터셋 수정에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @PutMapping(value = "/{dataId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<SuccessResponse<Void>> modifyData(
      @PathVariable @Min(1) Long dataId,
      @RequestPart(value = "dataFile") MultipartFile dataFile,
      @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
      @RequestPart @Validated ModifyDataWebRequest webRequest);

  /**
   * 지정한 ID의 데이터셋을 삭제합니다.
   *
   * @param dataId 삭제할 데이터셋의 ID (1 이상)
   * @return 삭제 성공 여부를 포함한 응답 객체
   */
  @Operation(summary = "데이터셋을 삭제한다.", description = "해당하는 데이터셋을 삭제한다.")
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
            description = "해당하는 데이터셋 삭제에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @DeleteMapping("/{dataId}")
  ResponseEntity<SuccessResponse<Void>> deleteData(@PathVariable @Min(1) Long dataId);

  /**
   * 삭제된 데이터셋을 복원한다.
   *
   * @param dataId 복원할 데이터셋의 ID (1 이상)
   * @return 복원 성공 시 성공 응답을 반환한다.
   */
  @Operation(summary = "데이터셋을 복원한다.", description = "해당하는 데이터셋을 복원한다.")
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
            description = "해당하는 데이터셋 복원에 성공했습니다.",
            useReturnTypeSchema = true)
      })
  @PatchMapping("/{dataId}/restore")
  ResponseEntity<SuccessResponse<Void>> restoreData(@PathVariable @Min(1) Long dataId);

  /**
   * 지정한 데이터셋 파일을 다운로드할 수 있는 사전 서명된 URL을 반환합니다.
   *
   * @param dataId 다운로드할 데이터셋의 ID (1 이상)
   * @return 사전 서명된 다운로드 URL 정보를 담은 성공 응답 객체
   */
  @Operation(summary = "해당 데이터셋의 파일을 다운로드한다.", description = "해당 데이터셋의 파일을 다운로드한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "해당 데이터셋의 파일을 다운로드한다.",
            useReturnTypeSchema = true)
      })
  @GetMapping("/{dataId}/download")
  ResponseEntity<SuccessResponse<GetDataPreSignedUrlWebResponse>> getPreSignedDataUrl(
      @PathVariable(name = "dataId") @Min(1) Long dataId);
}
