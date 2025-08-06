package com.dataracy.modules.comment.adapter.web.api.read;

import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.response.read.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.read.FindReplyCommentWebResponse;
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
import jakarta.servlet.http.HttpServletRequest;
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
public interface CommentReadApi {

    /**
     * 지정된 프로젝트의 댓글 목록을 페이지네이션하여 조회합니다.
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
            @Parameter(hidden = true)
            HttpServletRequest request,

            @PathVariable @Min(1)
            Long projectId,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    /**
     * 특정 프로젝트의 댓글에 대한 답글 목록을 페이지네이션하여 조회한다.
     *
     * @param projectId 답글을 조회할 프로젝트의 ID (1 이상)
     * @param commentId 답글을 조회할 부모 댓글의 ID (1 이상)
     * @param pageable  페이지네이션 정보 (기본: 페이지 0, 크기 5)
     * @return 답글 목록이 포함된 성공 응답
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
            @Parameter(hidden = true)
            HttpServletRequest request,

            @PathVariable @Min(1)
            Long projectId,

            @PathVariable @Min(1)
            Long commentId,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );
}
