package com.dataracy.modules.comment.adapter.web.api;

import com.dataracy.modules.comment.adapter.web.request.CommentModifyWebRequest;
import com.dataracy.modules.comment.adapter.web.request.CommentUploadWebRequest;
import com.dataracy.modules.comment.adapter.web.response.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.FindReplyCommentWebResponse;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "피드백 댓글 관련 API")
@RequestMapping("/api/v1/projects")
public interface CommentApi {

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
                    content = @Content(schema = @Schema(implementation = CommentUploadWebRequest.class))
            )
            @Validated @RequestBody
            CommentUploadWebRequest webRequest
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
                    content = @Content(schema = @Schema(implementation = CommentModifyWebRequest.class))
            )
            @Validated @RequestBody
            CommentModifyWebRequest webRequest
    );

    /****
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

    /**
     * 지정된 프로젝트의 댓글 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 댓글을 조회할 프로젝트의 ID (1 이상)
     * @param pageable 페이지네이션 정보 (기본: 페이지 0, 크기 5)
     * @return 프로젝트의 댓글 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "해당 프로젝트의 댓글 목록을 조회한다.",
            description = "해당 프로젝트의 댓글 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 프로젝트의 댓글 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{projectId}/comments")
    ResponseEntity<SuccessResponse<Page<FindCommentWebResponse>>> findComments(
            @PathVariable @Min(1)
            Long projectId,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    /**
     * 특정 프로젝트의 댓글에 대한 답글 목록을 페이지네이션하여 조회한다.
     *
     * @param projectId  조회할 프로젝트의 ID (1 이상)
     * @param commentId  답글을 조회할 부모 댓글의 ID (1 이상)
     * @param pageable   페이지네이션 정보 (기본: 페이지 0, 크기 5)
     * @return           답글 목록이 포함된 성공 응답
     */
    @Operation(
            summary = "해당 댓글 대한 답글 목록을 조회한다.",
            description = "해당 댓글 대한 답글 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 댓글 대한 답글 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/{projectId}/comments/{commentId}")
    ResponseEntity<SuccessResponse<Page<FindReplyCommentWebResponse>>> findReplyComments(
            @PathVariable @Min(1)
            Long projectId,

            @PathVariable @Min(1)
            Long commentId,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );
}
