package com.dataracy.modules.dataset.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.dataset.adapter.web.request.DataFilterWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Data", description = "데이터셋 관련 API")
@RequestMapping("/api/v1/datasets")
public interface DataApi {

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
            DataUploadWebRequest webRequest
    );

    /**
     * 지정한 데이터셋과 유사한 데이터셋 목록을 조회합니다.
     *
     * @param dataId 유사도를 기준으로 비교할 데이터셋의 ID (1 이상)
     * @param size 반환할 유사 데이터셋의 개수 (1 이상)
     * @return 유사한 데이터셋 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "유사한 데이터셋을 조회한다.",
            description = "제공받은 데이터와 유사한 데이터셋을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유사 데이터셋 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/search/similar")
    ResponseEntity<SuccessResponse<List<DataSimilarSearchWebResponse>>> searchSimilarDataSets(
            @RequestParam(name = "dataId")
            @Min(1)
            Long dataId,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 다운로드 수와 연결된 프로젝트 수를 기준으로 인기 있는 데이터셋 목록을 조회합니다.
     *
     * @param size 반환할 데이터셋의 최대 개수 (1 이상)
     * @return 인기 데이터셋 목록이 포함된 성공 응답 객체
     */
    @Operation(
            summary = "인기있는 데이터셋을 조회한다.",
            description = "다운로드가 많은, 연결된 프로젝트 개수가 많은 데이터셋을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기 있는 데이터셋 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/search/popular")
    ResponseEntity<SuccessResponse<List<DataPopularSearchWebResponse>>> searchPopularDataSets(
            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    @Operation(
            summary = "필터링된 데이터셋 리스트를 조회한다.",
            description = "필터링된 데이터셋 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터링된 데이터셋 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/search/filter")
    ResponseEntity<SuccessResponse<Page<DataFilterWebResponse>>> searchFilteredDataSets(
            @Validated @ModelAttribute
            DataFilterWebRequest webRequest,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    /**
     * 지정된 데이터셋의 세부 정보를 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터셋의 고유 식별자
     * @return 데이터셋의 상세 정보를 포함한 성공 응답
     */
    @Operation(
            summary = "데이터셋 세부정보를 조회한다.",
            description = "데이터셋 세부정보를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터셋 세부정보 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{dataId}")
    ResponseEntity<SuccessResponse<DataDetailWebResponse>> getDataDetail(
            @PathVariable(name = "dataId")
            Long dataId
    );

    @Operation(
            summary = "간단한 최신 데이터셋 목록을 조회한다.",
            description = "최신 데이터셋 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "간단한 최신 데이터셋 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/recent")
    ResponseEntity<SuccessResponse<List<DataMinimalSearchWebResponse>>> getRecentDataSets(
            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    @Operation(
            summary = "데이터셋 자동완성 검색 기능",
            description = "데이터셋 자동완성 검색 기능으로 데이터셋을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터셋 자동완성 검색 기능으로 데이터셋을 조회한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/real-time")
    ResponseEntity<SuccessResponse<List<DataMinimalSearchWebResponse>>> getRealTimeDataSets(
            @RequestParam
            String keyword,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    @Operation(
            summary = "카테고리별 데이터셋 개수를 카운트한다.",
            description = "카테고리별 데이터셋 개수를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리별 데이터셋 개수를 조회한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/group-by/topic")
    ResponseEntity<SuccessResponse<List<CountDataGroupWebResponse>>> countDataSetsByTopicLabel();
}
