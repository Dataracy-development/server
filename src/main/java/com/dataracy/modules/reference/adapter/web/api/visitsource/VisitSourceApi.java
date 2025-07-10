package com.dataracy.modules.reference.adapter.web.api.visitsource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.response.AllVisitSourcesWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Reference", description = "참조 데이터 관련 API")
@RequestMapping("/api/v1/visit-sources")
public interface VisitSourceApi {
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
    @GetMapping
    ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> allVisitSources();
}
