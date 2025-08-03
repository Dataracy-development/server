package com.dataracy.modules.project.adapter.web.api.search;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.project.adapter.web.request.search.FilteringProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.search.FilteredProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 관련 API")
@RequestMapping("/api/v1/projects")
public interface ProjectSearchApi {
    /**
     * 실시간 키워드 자동완성을 위해 프로젝트 목록을 조회합니다.
     *
     * @param keyword 자동완성에 사용할 검색 키워드입니다. 입력하지 않으면 전체에서 조회합니다.
     * @param size 반환할 프로젝트 최대 개수로, 1 이상이어야 합니다.
     * @return 실시간 검색 결과에 해당하는 프로젝트 목록이 포함된 성공 응답 객체
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
    ResponseEntity<SuccessResponse<List<RealTimeProjectWebResponse>>> searchRealTimeProjects(
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
    ResponseEntity<SuccessResponse<List<SimilarProjectWebResponse>>> searchSimilarProjects(
            @PathVariable(name = "projectId")
            @Min(1)
            Long projectId,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /****
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
    ResponseEntity<SuccessResponse<List<PopularProjectWebResponse>>> searchPopularProjects(
            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 필터 조건에 따라 프로젝트 목록을 페이지네이션하여 조회한다.
     *
     * @param webRequest 프로젝트 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 정보 (기본: 1페이지, 5개씩)
     * @return 필터링 조건에 맞는 프로젝트 목록의 페이지를 성공 응답으로 반환한다.
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
    ResponseEntity<SuccessResponse<Page<FilteredProjectWebResponse>>> searchFilteredProjects(
            @Validated @ModelAttribute
            FilteringProjectWebRequest webRequest,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );
}
