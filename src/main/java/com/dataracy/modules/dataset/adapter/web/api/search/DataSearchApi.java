package com.dataracy.modules.dataset.adapter.web.api.search;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Data - Search", description = "데이터셋 관련 API - 검색")
@RequestMapping("/api/v1/datasets")
public interface DataSearchApi {
    /**
     * 지정한 데이터셋과 유사한 데이터셋 목록을 반환합니다.
     *
     * @param dataId 유사한 데이터셋을 찾을 기준 데이터셋의 ID (1 이상)
     * @param size 반환할 유사 데이터셋의 최대 개수 (1 이상)
     * @return 유사한 데이터셋 목록이 포함된 성공 응답 객체
     */
    @Operation(
            summary = "유사한 데이터셋을 조회한다.",
            description = "제공받은 데이터와 유사한 데이터셋을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유사 데이터셋 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/{dataId}/similar")
    ResponseEntity<SuccessResponse<List<SimilarDataWebResponse>>> searchSimilarDataSets(
            @PathVariable(name = "dataId")
            @Min(1)
            Long dataId,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );

    /**
     * 필터 조건에 따라 데이터셋 목록을 페이지네이션하여 조회한다.
     *
     * @param webRequest 데이터셋 필터링 조건이 포함된 요청 객체
     * @param pageable 결과 페이지네이션 정보
     * @return 필터링 조건에 부합하는 데이터셋 목록의 페이지 결과를 포함한 성공 응답
     */
    @Operation(
            summary = "필터링된 데이터셋 리스트를 조회한다.",
            description = "필터링된 데이터셋 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터링된 데이터셋 리스트 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/filter")
    ResponseEntity<SuccessResponse<Page<FilteredDataWebResponse>>> searchFilteredDataSets(
            @Validated @ModelAttribute
            FilteringDataWebRequest webRequest,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    /**
     * 키워드 자동완성 검색을 통해 조건에 맞는 데이터셋의 최소 정보를 리스트로 반환합니다.
     *
     * @param keyword 자동완성 검색에 사용할 키워드(선택 사항)
     * @param size 반환할 데이터셋 개수(최소 1)
     * @return 자동완성 키워드에 부합하는 데이터셋 최소 정보 목록을 포함한 성공 응답
     */
    @Operation(
            summary = "키워드 자동완성을 위한 데이터셋 검색 기능",
            description = "데이터셋 자동완성 검색 기능으로 데이터셋을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터셋 자동완성 검색 기능으로 데이터셋을 조회한다.", useReturnTypeSchema = true)
    })
    @GetMapping("/search/real-time")
    ResponseEntity<SuccessResponse<List<RecentMinimalDataWebResponse>>> getRealTimeDataSets(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );
}
