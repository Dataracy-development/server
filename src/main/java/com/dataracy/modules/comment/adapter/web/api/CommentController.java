package com.dataracy.modules.comment.adapter.web.api;

import com.dataracy.modules.comment.adapter.web.mapper.CommentWebMapper;
import com.dataracy.modules.comment.adapter.web.request.CommentModifyWebRequest;
import com.dataracy.modules.comment.adapter.web.request.CommentUploadWebRequest;
import com.dataracy.modules.comment.adapter.web.response.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;
import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.port.in.*;
import com.dataracy.modules.comment.domain.status.CommentSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.AuthorizationCommentEdit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentWebMapper commentWebMapper;

    private final CommentUploadUseCase commentUploadUseCase;
    private final CommentModifyUseCase commentModifyUseCase;
    private final CommentDeleteUseCase commentDeleteUseCase;
    private final FindCommentListUseCase findCommentListUseCase;
    private final FindReplyCommentListUseCase findReplyCommentListUseCase;

    /**
     * 프로젝트에 새로운 댓글을 등록합니다.
     *
     * @param projectId 댓글이 등록될 프로젝트의 ID
     * @param userId 댓글을 작성하는 사용자의 ID
     * @param webRequest 댓글 등록 요청 정보
     * @return 댓글 등록 성공 시 201 Created 상태와 함께 성공 응답을 반환합니다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadComment(Long projectId, Long userId, CommentUploadWebRequest webRequest) {
        CommentUploadRequest requestDto = commentWebMapper.toApplicationDto(webRequest);
        commentUploadUseCase.upload(projectId, userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(CommentSuccessStatus.CREATED_COMMENT));
    }

    /**
     * 프로젝트 내 특정 댓글을 수정합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param commentId 수정할 댓글의 ID
     * @param webRequest 댓글 수정 요청 데이터
     * @return 댓글 수정 성공 상태를 포함한 HTTP 200 OK 응답
     */
    @Override
    @AuthorizationCommentEdit
    public ResponseEntity<SuccessResponse<Void>> modifyComment(Long projectId, Long commentId, CommentModifyWebRequest webRequest) {
        CommentModifyRequest requestDto = commentWebMapper.toApplicationDto(webRequest);
        commentModifyUseCase.modify(projectId, commentId, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.MODIFY_COMMENT));
    }

    /**
     * 프로젝트 내에서 지정된 댓글을 삭제합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param commentId 삭제할 댓글의 ID
     * @return 댓글 삭제 성공 상태를 포함한 HTTP 200 OK 응답
     */
    @Override
    @AuthorizationCommentEdit
    public ResponseEntity<SuccessResponse<Void>> deleteComment(Long projectId, Long commentId) {
        commentDeleteUseCase.delete(projectId, commentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.DELETE_COMMENT));
    }

    /**
     * 프로젝트의 댓글 목록을 페이지네이션하여 조회합니다.
     *
     * @param projectId 댓글을 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 조회된 댓글 목록과 성공 상태가 포함된 HTTP 200 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<FindCommentWebResponse>>> findComments(Long projectId, Pageable pageable) {
        Page<FindCommentResponse> responseDto = findCommentListUseCase.findComments(projectId, pageable);
        Page<FindCommentWebResponse> webResponse = responseDto.map(commentWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.GET_COMMENTS, webResponse));
    }

    /**
     * 프로젝트 내 특정 댓글에 대한 답글 목록을 페이징하여 조회합니다.
     *
     * @param projectId  프로젝트의 고유 식별자
     * @param commentId  답글을 조회할 대상 댓글의 고유 식별자
     * @param pageable   페이징 및 정렬 정보를 포함하는 객체
     * @return           답글 목록과 성공 상태가 포함된 HTTP 200 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<FindReplyCommentWebResponse>>> findReplyComments(Long projectId, Long commentId, Pageable pageable) {
        Page<FindReplyCommentResponse> responseDto = findReplyCommentListUseCase.findReplyComments(projectId, commentId, pageable);
        Page<FindReplyCommentWebResponse> webResponse = responseDto.map(commentWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.GET_REPLY_COMMENTS, webResponse));
    }
}
