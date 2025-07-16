package com.dataracy.modules.data.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.data.adapter.web.request.DataUploadWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Data", description = "데이터셋 관련 API")
@RequestMapping("/api/v1/datasets")
public interface DataApi {

    /****
     * 데이터셋 파일과 메타데이터를 업로드하여 데이터셋 정보를 저장한다.
     *
     * @param dataFile 업로드할 데이터셋 파일
     * @param thumbnailFile 데이터셋 썸네일 이미지 파일 (선택 사항)
     * @param webRequest 데이터셋 메타데이터 및 추가 정보가 담긴 요청 객체
     * @return 업로드 성공 여부를 나타내는 성공 응답
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
            DataUploadWebRequest webRequest
    );
}
