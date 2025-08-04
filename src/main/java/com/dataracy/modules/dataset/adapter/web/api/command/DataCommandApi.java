package com.dataracy.modules.dataset.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Data", description = "데이터셋 관련 API")
@RequestMapping("/api/v1/datasets")
public interface DataCommandApi {

    /**
     * 데이터셋 파일, 썸네일(선택), 메타데이터를 업로드하여 새로운 데이터셋 정보를 저장한다.
     *
     * @param dataFile 업로드할 데이터셋 파일
     * @param thumbnailFile 데이터셋 썸네일 이미지 파일 (선택 사항)
     * @param webRequest 데이터셋의 메타데이터 및 추가 정보를 포함하는 요청 객체
     * @return 업로드 성공 시 성공 응답을 반환
     */
    @Operation(
            summary = "데이터셋을 업로드한다.",
            description = "제공받은 웹 요청 DTO의 데이터셋 정보를 통해 데이터셋 정보를 db에 저장한다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "데이터셋 업로드에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> uploadData(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @RequestPart(value = "dataFile") MultipartFile dataFile,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart @Validated
            UploadDataWebRequest webRequest
    );

    /**
     * 지정한 데이터셋 ID에 해당하는 데이터셋을 새로운 파일과 메타데이터로 수정합니다.
     *
     * @param dataId 수정할 데이터셋의 고유 ID (1 이상)
     * @param dataFile 필수 데이터셋 파일
     * @param thumbnailFile 선택적 썸네일 파일
     * @param webRequest 데이터셋 수정 정보를 담은 요청 DTO
     * @return 수정 성공 여부를 나타내는 응답
     */
    @Operation(
            summary = "데이터셋 수정한다.",
            description = "제공받은 웹 요청 DTO의 데이터셋 정보를 통해 기존 데이터셋을 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터셋 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PutMapping(value="/{dataId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyData(
            @PathVariable @Min(1)
            Long dataId,

            @RequestPart(value = "dataFile") MultipartFile dataFile,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,

            @RequestPart @Validated
            ModifyDataWebRequest webRequest
    );

    /**
     * 지정한 데이터셋을 삭제합니다.
     *
     * @param dataId 삭제할 데이터셋의 ID (1 이상)
     * @return 삭제 성공 여부를 포함한 응답
     */
    @Operation(
            summary = "데이터셋을 삭제한다.",
            description = "해당하는 데이터셋을 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당하는 데이터셋 삭제에  성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @DeleteMapping("/{dataId}")
    ResponseEntity<SuccessResponse<Void>> deleteData(
            @PathVariable @Min(1)
            Long dataId
    );

    /**
     * 삭제된 데이터셋을 복원한다.
     *
     * @param dataId 복원할 데이터셋의 ID (1 이상)
     * @return 복원 성공 시 성공 응답을 반환한다.
     */
    @Operation(
            summary = "데이터셋을 복원한다.",
            description = "해당하는 데이터셋을 복원한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당하는 데이터셋 복원에  성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/{dataId}/restore")
    ResponseEntity<SuccessResponse<Void>> restoreData(
            @PathVariable @Min(1)
            Long dataId
    );

    /**
     * 지정한 데이터셋의 파일을 다운로드할 수 있는 사전 서명된 URL을 반환합니다.
     *
     * @param dataId 다운로드할 데이터셋의 ID (1 이상)
     * @return 데이터셋 파일 다운로드를 위한 사전 서명된 URL이 포함된 성공 응답
     */
    @Operation(
            summary = "해당 데이터셋의 파일을 다운로드한다.",
            description = "해당 데이터셋의 파일을 다운로드한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 데이터셋의 파일을 다운로드한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{dataId}/download")
    ResponseEntity<SuccessResponse<String>> getPreSignedDataUrl(
            @PathVariable(name = "dataId") @Min(1)
            Long dataId
    );
}
