package com.dataracy.modules.project.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.project.adapter.web.response.read.ConnectedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ContinuedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ProjectDetailWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 관련 API")
@RequestMapping("/api/v1/projects")
public interface ProjectReadApi {
    /**
     * 지정한 프로젝트의 상세 정보를 조회하여 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID (1 이상)
     * @return 프로젝트 상세 정보를 포함한 성공 응답 객체
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
            @Parameter(hidden = true)
            HttpServletRequest request,

            @Parameter(hidden = true)
            HttpServletResponse response,

            @PathVariable @Min(1)
            Long projectId
    );

    /**
     * 지정한 프로젝트를 기준으로 이어지는(연속) 프로젝트들의 목록을 페이지 단위로 조회한다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID (1 이상)
     * @param pageable 결과 페이지 및 크기 정보 (기본: 페이지 0, 크기 3)
     * @return 이어가기 프로젝트 목록이 포함된 성공 응답 객체
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
    ResponseEntity<SuccessResponse<Page<ContinuedProjectWebResponse>>> findContinueProjects(
            @PathVariable @Min(1)
            Long projectId,

            @PageableDefault(size = 3, page = 0)
            Pageable pageable
    );

    /**
     * 특정 데이터셋에 연결된 프로젝트들의 목록을 페이지 단위로 반환합니다.
     *
     * @param dataId 연결된 프로젝트를 조회할 데이터셋의 식별자 (1 이상)
     * @param pageable 페이지네이션 정보 (기본: 0페이지, 3개씩)
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
    ResponseEntity<SuccessResponse<Page<ConnectedProjectWebResponse>>> findConnectedProjectsAssociatedWithData(
            @RequestParam @Min(1)
            Long dataId,

            @PageableDefault(size = 3, page = 0)
            Pageable pageable
    );

    /**
     * 좋아요, 댓글, 조회수를 기준으로 인기 프로젝트 목록을 조회한다.
     *
     * @param size 반환할 최대 프로젝트 개수 (1 이상)
     * @return 인기 프로젝트 목록이 포함된 성공 응답 객체
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
    ResponseEntity<SuccessResponse<List<PopularProjectWebResponse>>> getPopularProjects(
            @RequestParam(name = "size")
            @Min(1)
            int size
    );
}
