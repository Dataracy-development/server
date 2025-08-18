package com.dataracy.modules.project.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.command.UploadProjectWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

@Tag(name = "Project - Command", description = "프로젝트 관련 API - 생성, 수정, 삭제 등")
@RequestMapping("/api/v1/projects")
public interface ProjectCommandApi {
    /**
     * 새로운 프로젝트 정보를 업로드하여 데이터베이스에 저장한다.
     *
     * @param thumbnailFile 프로젝트 썸네일 파일 (선택 사항)
     * @param webRequest 업로드할 프로젝트 정보가 포함된 요청 객체
     * @return 업로드 성공 시 201 Created 상태와 함께 성공 응답을 반환한다.
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
            @ApiResponse(responseCode = "201", description = "프로젝트 업로드에 성공했습니다.", useReturnTypeSchema = true)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<UploadProjectWebResponse>> uploadProject(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @RequestPart(value = "thumbnailFile", required = false)
            MultipartFile thumbnailFile,

            @RequestPart @Validated
            UploadProjectWebRequest webRequest
    );

    /**
     * 기존 프로젝트를 전달받은 정보와 선택적 썸네일 파일로 수정합니다.
     *
     * @param projectId 수정할 프로젝트의 고유 ID (1 이상)
     * @param thumbnailFile 프로젝트와 연관된 썸네일 파일 (선택 사항)
     * @param webRequest 프로젝트 수정 요청 데이터
     * @return 수정 성공 시 성공 응답을 반환합니다.
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
            @ApiResponse(responseCode = "200", description = "프로젝트 수정에 성공했습니다.", useReturnTypeSchema = true)
    })
    @PutMapping(value="/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyProject(
            @PathVariable @Min(1)
            Long projectId,

            @RequestPart(value = "thumbnailFile", required = false)
            MultipartFile thumbnailFile,

            @RequestPart @Validated
            ModifyProjectWebRequest webRequest
    );

    /**
     * 지정한 ID의 프로젝트를 삭제합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID (1 이상)
     * @return 삭제 성공 시 성공 응답을 반환합니다.
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
            @ApiResponse(responseCode = "200", description = "해당하는 프로젝트 삭제에  성공했습니다.", useReturnTypeSchema = true)
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
     * @return 복원 성공 시 성공 응답을 반환한다.
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
            @ApiResponse(responseCode = "200", description = "해당하는 프로젝트 복원에  성공했습니다.", useReturnTypeSchema = true)
    })
    @PatchMapping("/{projectId}/restore")
    ResponseEntity<SuccessResponse<Void>> restoreProject(
            @PathVariable @Min(1)
            Long projectId
    );
}
