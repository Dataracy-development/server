package com.dataracy.modules.reference.adapter.web.api.datasource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataSourcesWebResponse;
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
@RequestMapping("/api/v1/references")
public interface DataSourceApi {
    /**
     * 데이터 소스의 전체 목록을 조회한다.
     *
     * @return 전체 데이터 소스 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "전체 데이터 출처 리스트를 조회",
            description = "DB에서 전체 데이터 출처 리스트를 조회한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 데이터 출처 리스트 조회",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @GetMapping("/data-sources")
    ResponseEntity<SuccessResponse<AllDataSourcesWebResponse>> findAllDataSources();
}
