package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.project.adapter.web.request.ProjectFilterWebRequest;
import com.dataracy.modules.project.adapter.web.request.ProjectModifyWebRequest;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.adapter.web.response.*;
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

@Tag(name = "Project", description = "프로젝트 관련 API")
@RequestMapping("/api/v1/projects")
public interface ProjectApi {

    /**
     * 프로젝트 정보를 업로드하고 데이터베이스에 저장한다.
     *
     * @param file 프로젝트와 연관된 파일(선택 사항)
     * @param webRequest 업로드할 프로젝트 정보가 담긴 요청 객체
     * @return 업로드 성공 시 201 Created와 함께 성공 응답을 반환한다.
     */
    @Operation(
            summary = "프로젝트를 업로드한다.",
            description = "제공받은 웹 요청 DTO의 프로젝트 정보를 통해 프로젝트 정보를 db에 저장한다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "프로젝트 업로드에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> uploadProject(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @RequestPart(value = "file", required = false) MultipartFile file,

            @RequestPart @Validated
            ProjectUploadWebRequest webRequest
    );

    /**
     * 키워드 자동완성을 위해 실시간으로 프로젝트 목록을 조회합니다.
     *
     * @param keyword 자동완성에 사용할 검색 키워드 (선택 사항)
     * @param size 반환할 프로젝트 최대 개수 (1 이상)
     * @return 실시간 검색 결과로 조회된 프로젝트 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "키워드 자동완성을 위한 실시간으로 프로젝트 리스트를 조회한다.",
            description = "제공받은 키워드와 사이즈를 토대로 실시간으로 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "실시간 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/search/real-time")
    ResponseEntity<SuccessResponse<List<ProjectRealTimeSearchWebResponse>>> searchRealTimeProjects(
            @RequestParam(name = "keyword", required = false)
            String keyword,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 지정한 프로젝트와 유사한 프로젝트 목록을 조회합니다.
     *
     * @param projectId 유사도를 비교할 기준이 되는 프로젝트의 ID
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사한 프로젝트 목록이 포함된 성공 응답 객체
     */
    @Operation(
            summary = "유사 프로젝트 리스트를 조회한다.",
            description = "해당하는 프로젝트와 유사한 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유사 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{projectId}/similar")
    ResponseEntity<SuccessResponse<List<ProjectSimilarSearchWebResponse>>> searchSimilarProjects(
            @PathVariable(name = "projectId")
            @Min(1)
            Long projectId,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 좋아요, 댓글, 조회수를 기준으로 인기 프로젝트 목록을 반환한다.
     *
     * @param size 반환할 프로젝트의 최대 개수 (1 이상)
     * @return 인기 프로젝트 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "인기있는 프로젝트 리스트를 조회한다.",
            description = "좋아요, 댓글, 조회수를 기준으로 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기있는 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/popular")
    ResponseEntity<SuccessResponse<List<ProjectPopularSearchWebResponse>>> searchPopularProjects(
            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 필터 조건에 따라 프로젝트 목록을 페이지네이션하여 조회한다.
     *
     * @param webRequest 프로젝트 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 정보 (기본: 1페이지, 5개씩)
     * @return 필터링된 프로젝트 목록의 페이지를 담은 성공 응답
     */
    @Operation(
            summary = "필터링된 프로젝트 리스트를 조회한다.",
            description = "필터링된 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터링된 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/filter")
    ResponseEntity<SuccessResponse<Page<ProjectFilterWebResponse>>> searchFilteredProjects(
            @Validated @ModelAttribute
            ProjectFilterWebRequest webRequest,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    /**
     * 프로젝트 ID로 해당 프로젝트의 상세 정보를 조회하여 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID (1 이상)
     * @return 프로젝트 상세 정보를 포함한 성공 응답
     */
    @Operation(
            summary = "프로젝트 상세 정보를 조회한다.",
            description = "프로젝트 상세 정보를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 상세 정보 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{projectId}")
    ResponseEntity<SuccessResponse<ProjectDetailWebResponse>> getProjectDetail(
            @PathVariable @Min(1)
            Long projectId
    );

    /**
     * 지정한 프로젝트의 이어가기(연속) 프로젝트 목록을 페이지 단위로 조회한다.
     *
     * @param projectId 이어가기 프로젝트를 조회할 기준 프로젝트의 ID (1 이상)
     * @param pageable 페이지네이션 정보 (기본: 페이지 0, 크기 3)
     * @return 이어가기 프로젝트 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "해당하는 프로젝트의 이어가기 프로젝트 리스트를 조회한다.",
            description = "해당하는 프로젝트의 이어가기 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이어가기 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{projectId}/continue")
    ResponseEntity<SuccessResponse<Page<ContinueProjectWebResponse>>> searchContinueProjects(
            @PathVariable @Min(1)
            Long projectId,

            @PageableDefault(size = 3, page = 0)
            Pageable pageable
    );

    /**
     * 지정된 데이터셋과 연결된 프로젝트 목록을 페이지네로 조회합니다.
     *
     * @param dataId 조회할 데이터셋의 고유 식별자
     * @param pageable 결과 페이지네이션 정보 (기본 페이지 크기: 3)
     * @return 연결된 프로젝트 목록이 포함된 성공 응답 객체
     */
    @Operation(
            summary = "데이터와 연결된 프로젝트 리스트를 조회한다.",
            description = "데이터와 연결된 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터와 연결된 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/connected-to-dataset")
    ResponseEntity<SuccessResponse<Page<ConnectedProjectAssociatedWithDataWebResponse>>> searchConnectedProjectsAssociatedWithData(
            @RequestParam @Min(1)
            Long dataId,

            @PageableDefault(size = 3, page = 0)
            Pageable pageable
    );

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트를 전달받은 데이터로 수정합니다.
     *
     * @param projectId 수정할 프로젝트의 ID (1 이상)
     * @param file      프로젝트와 연관된 파일 (선택 사항)
     * @param webRequest 프로젝트 수정 정보를 담은 요청 객체
     * @return 프로젝트 수정 성공 여부를 나타내는 응답
     */
    @Operation(
            summary = "프로젝트를 수정한다.",
            description = "제공받은 웹 요청 DTO의 프로젝트 정보를 통해 기존 프로젝트를 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PutMapping(value="/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyProject(
            @PathVariable @Min(1)
            Long projectId,

            @RequestPart(value = "file", required = false) MultipartFile file,

            @RequestPart @Validated
            ProjectModifyWebRequest webRequest
    );

    /**
     * 지정한 프로젝트를 삭제합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID (1 이상)
     * @return 삭제 성공 여부를 포함한 응답
     */
    @Operation(
            summary = "프로젝트를 삭제한다.",
            description = "해당하는 프로젝트를 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당하는 프로젝트 삭제에  성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @DeleteMapping("/{projectId}")
    ResponseEntity<SuccessResponse<Void>> deleteProject(
            @PathVariable @Min(1)
            Long projectId
    );

    /**
     * 삭제된 프로젝트를 복원한다.
     *
     * @param projectId 복원할 프로젝트의 ID (1 이상)
     * @return 복원 성공 여부를 담은 성공 응답
     */
    @Operation(
            summary = "프로젝트를 복원한다.",
            description = "해당하는 프로젝트를 복원한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당하는 프로젝트 복원에  성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PatchMapping("/{projectId}/restore")
    ResponseEntity<SuccessResponse<Void>> restoreProject(
            @PathVariable @Min(1)
            Long projectId
    );
}
