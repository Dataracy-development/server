package com.dataracy.modules.comment.adapter.web.api.command;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
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

@Tag(name = "Comment", description = "피드백 댓글 관련 API")
@RequestMapping("/api/v1/projects")
public interface CommentCommandApi {

    /**
     * 지정한 프로젝트에 피드백 댓글을 작성합니다.
     *
     * @param projectId 피드백 댓글을 작성할 프로젝트의 ID (1 이상)
     * @param webRequest 댓글 내용 및 (선택적으로) 부모 댓글 ID를 포함한 요청 본문
     * @return 댓글 작성 성공 시 201 상태 코드와 함께 반환되는 성공 응답
     */
    @Operation(
            summary = "프로젝트에 피드백 댓글을 작성한다.",
            description = "해당 프로젝트에 대한 피드백 댓글을 작성한다."
    )
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "피드백 댓글 작성에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/{projectId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> uploadComment(
            @PathVariable @Min(1)
            Long projectId,

            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "댓글 내용. `parentCommentId`가 있으면 답글로 처리됩니다.",
                    content = @Content(schema = @Schema(implementation = UploadCommentWebRequest.class))
            )
            @Validated @RequestBody
            UploadCommentWebRequest webRequest
    );

    /**
     * 프로젝트의 특정 댓글을 수정합니다.
     *
     * @param projectId  댓글이 속한 프로젝트의 ID (1 이상)
     * @param commentId  수정할 댓글의 ID (1 이상)
     * @param webRequest 수정할 댓글 내용을 포함한 요청 객체
     * @return 댓글 수정 성공 여부를 나타내는 응답
     */
    @Operation(
            summary = "댓글을 수정한다.",
            description = "프로젝트의 해당하는 댓글을 수정한다."
    )
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PutMapping(value="/{projectId}/comments/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyComment(
            @PathVariable @Min(1)
            Long projectId,

            @PathVariable @Min(1)
            Long commentId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "수정할 댓글 내용.",
                    content = @Content(schema = @Schema(implementation = ModifyCommentWebRequest.class))
            )
            @Validated @RequestBody
            ModifyCommentWebRequest webRequest
    );

    /**
     * 지정된 프로젝트의 피드백 댓글을 삭제합니다.
     *
     * @param projectId 피드백 댓글이 속한 프로젝트의 ID (1 이상)
     * @param commentId 삭제할 피드백 댓글의 ID (1 이상)
     * @return 삭제 성공 여부를 나타내는 성공 응답
     */
    @Operation(
            summary = "피드백 댓글을 삭제한다.",
            description = "해당 피드백 댓글을 삭제한다."
    )
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당하는 피드백 댓글 삭제에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @DeleteMapping("/{projectId}/comments/{commentId}")
    ResponseEntity<SuccessResponse<Void>> deleteComment(
            @PathVariable @Min(1)
            Long projectId,

            @PathVariable @Min(1)
            Long commentId
    );
}
