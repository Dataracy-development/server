package com.dataracy.modules.dataset.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.dataset.adapter.web.request.DataFilterWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.DataModifyWebRequest;
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
     * 지정한 데이터셋과 유사한 데이터셋 목록을 반환합니다.
     *
     * @param dataId 유사도를 기준으로 비교할 데이터셋의 고유 ID (1 이상)
     * @param size 반환할 유사 데이터셋의 최대 개수 (1 이상)
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
    @GetMapping("/{dataId}/similar")
    ResponseEntity<SuccessResponse<List<DataSimilarSearchWebResponse>>> searchSimilarDataSets(
            @PathVariable(name = "dataId")
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
     * @return 인기 데이터셋 목록이 포함된 성공 응답
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
    @GetMapping("/popular")
    ResponseEntity<SuccessResponse<List<DataPopularSearchWebResponse>>> searchPopularDataSets(
            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 필터 조건에 따라 데이터셋 목록을 페이지 단위로 조회한다.
     *
     * @param webRequest 데이터셋 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 정보
     * @return 필터링 조건에 맞는 데이터셋의 페이지 결과를 포함하는 성공 응답
     */
    @Operation(
            summary = "필터링된 데이터셋 리스트를 조회한다.",
            description = "필터링된 데이터셋 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터링된 데이터셋 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/filter")
    ResponseEntity<SuccessResponse<Page<DataFilterWebResponse>>> searchFilteredDataSets(
            @Validated @ModelAttribute
            DataFilterWebRequest webRequest,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    /**
     * 데이터셋의 고유 식별자를 기반으로 상세 정보를 반환합니다.
     *
     * @param dataId 상세 정보를 조회할 데이터셋의 고유 식별자
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

    /**
     * 최신에 추가된 데이터셋의 간단한 목록을 조회합니다.
     *
     * @param size 반환할 데이터셋 개수 (1 이상)
     * @return 최신 데이터셋의 최소 정보 목록이 포함된 성공 응답
     */
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

    /**
     * 키워드 자동완성 검색을 통해 데이터셋의 최소 정보를 리스트로 반환합니다.
     *
     * @param keyword 자동완성에 사용할 검색 키워드(선택 사항)
     * @param size 반환할 데이터셋 개수(최소 1)
     * @return 자동완성 결과로 조회된 데이터셋 최소 정보 목록을 포함한 성공 응답
     */
    @Operation(
            summary = "키워드 자동완성을 위한 데이터셋 검색 기능",
            description = "데이터셋 자동완성 검색 기능으로 데이터셋을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터셋 자동완성 검색 기능으로 데이터셋을 조회한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/search/real-time")
    ResponseEntity<SuccessResponse<List<DataMinimalSearchWebResponse>>> getRealTimeDataSets(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 데이터셋을 카테고리(토픽)별로 그룹화하여 각 카테고리별 데이터셋의 개수를 반환합니다.
     *
     * @return 각 카테고리별 데이터셋 개수 정보를 담은 성공 응답
     */
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

    /**
     * 프로젝트에 연결된 데이터셋 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 데이터셋을 조회할 프로젝트의 고유 ID (1 이상)
     * @param pageable 페이지네이션 정보 (기본 페이지 크기 3, 0페이지부터 시작)
     * @return 프로젝트와 연결된 데이터셋 목록을 포함하는 성공 응답
     */
    @Operation(
            summary = "프로젝트와 연결된 데이터셋 리스트를 조회한다.",
            description = "프로젝트와 연결된 데이터셋 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트와 연결된 데이터셋 리스트를 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/connected-to-project")
    ResponseEntity<SuccessResponse<Page<ConnectedDataAssociatedWithProjectWebResponse>>> searchConnectedDataSetsAssociatedWithProject(
            @RequestParam @Min(1)
            Long projectId,

            @PageableDefault(size = 3, page = 0)
            Pageable pageable
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
    ResponseEntity<SuccessResponse<Void>> modifyDataSet(
            @PathVariable @Min(1)
            Long dataId,

            @RequestPart(value = "dataFile") MultipartFile dataFile,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,

            @RequestPart @Validated
            DataModifyWebRequest webRequest
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
    ResponseEntity<SuccessResponse<Void>> deleteDataSet(
            @PathVariable @Min(1)
            Long dataId
    );

    /**
     * 삭제된 데이터셋을 복원한다.
     *
     * @param dataId 복원할 데이터셋의 ID (1 이상)
     * @return 복원 성공 여부를 나타내는 응답
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
    ResponseEntity<SuccessResponse<Void>> restoreDataSet(
            @PathVariable @Min(1)
            Long dataId
    );

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
            @PathVariable(name = "dataId")
            Long dataId
    );
}
