package com.dataracy.modules.reference.adapter.web.api.visitsource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Reference", description = "참조 데이터 관련 API")
@RequestMapping("/api/v1/references")
public interface VisitSourceApi {
    /**
     * 전체 방문 경로(visit source) 목록을 조회한다.
     * 데이터베이스에 저장된 모든 방문 경로 정보를 반환한다.
     *
     * @return 전체 방문 경로 목록이 포함된 성공 응답 객체
     */
    @Operation(
            summary = "전체 방문 경로 리스트를 조회",
            description = "DB에서 전체 방문 경로 리스트를 조회한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 방문 경로 리스트 조회", useReturnTypeSchema = true),
    })
    @GetMapping("/visit-sources")
    ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> findAllVisitSources();
}
