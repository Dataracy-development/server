package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.adapter.web.response.ProjectRealTimeSearchWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
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
     * 주어진 키워드와 결과 개수 제한을 기준으로 실시간 프로젝트 리스트를 조회합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 프로젝트 리스트의 최대 개수 (1 이상)
     * @return 실시간 검색 결과로 조회된 프로젝트 리스트가 포함된 성공 응답
     */
    @Operation(
            summary = "실시간으로 프로젝트 리스트를 조회한다.",
            description = "제공받은 키워드와 사이즈를 토대로 실시간으로 프로젝트 리스트를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "실시간 프로젝트 리스트 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/search/real-time")
    ResponseEntity<SuccessResponse<List<ProjectRealTimeSearchWebResponse>>> search(
            @RequestParam(name = "keyword")
            String keyword,

            @RequestParam(name = "size")
            @Min(1)
            int size
    );
}
