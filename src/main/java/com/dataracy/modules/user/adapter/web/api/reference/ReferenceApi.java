package com.dataracy.modules.user.adapter.web.api.reference;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.response.reference.AllAuthorLevelsWebResponse;
import com.dataracy.modules.user.adapter.web.response.reference.AllOccupationsWebResponse;
import com.dataracy.modules.user.adapter.web.response.reference.AllVisitSourcesWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Reference", description = "Referece 관련 API")
@RequestMapping("/api/v1")
public interface ReferenceApi {
    /**
     * 전체 authorLevel 리스트를 조회한다.
     *
     * @return 전체 authorLevel 리스트
     */
    @Operation(
            summary = "전체 작성자 유형 리스트를 조회",
            description = "DB에서 전체 작성자 유형 리스트를 조회한다..",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 작성자 유형 리스트 조회",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @GetMapping(value = "/author-levels")
    ResponseEntity<SuccessResponse<AllAuthorLevelsWebResponse>> allAuthorLevels();

    /**
     * 전체 occupation 리스트를 조회한다.
     *
     * @return 전체 occupation 리스트
     */
    @Operation(
            summary = "전체 직업 리스트를 조회",
            description = "DB에서 전체 직업 리스트를 조회한다..",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 직업 리스트 조회",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @GetMapping(value = "/occupations")
    ResponseEntity<SuccessResponse<AllOccupationsWebResponse>> allOccupations();

    /**
     * 전체 visitSource 리스트를 조회한다.
     *
     * @return 전체 visitSource 리스트
     */
    @Operation(
            summary = "전체 방문 경로 리스트를 조회",
            description = "DB에서 전체 방문 경로 리스트를 조회한다..",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 방문 경로 리스트 조회",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @GetMapping(value = "/visit-sources")
    ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> allVisitSources();
}
