package com.dataracy.modules.project.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

@Tag(name = "Project", description = "프로젝트 관련 API")
@RequestMapping("/api/v1/projects")
public interface ProjectCommandApi {
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
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
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

            @RequestPart(value = "file", required = false)
            MultipartFile file,

            @RequestPart @Validated
            UploadProjectWebRequest webRequest
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
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PutMapping(value="/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyProject(
            @PathVariable @Min(1)
            Long projectId,

            @RequestPart(value = "file", required = false)
            MultipartFile file,

            @RequestPart @Validated
            ModifyProjectWebRequest webRequest
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
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
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
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
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
