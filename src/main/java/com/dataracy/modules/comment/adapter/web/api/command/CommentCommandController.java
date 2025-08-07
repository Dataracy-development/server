package com.dataracy.modules.comment.adapter.web.api.command;

import com.dataracy.modules.comment.adapter.web.mapper.command.CommandCommentWebMapper;
import com.dataracy.modules.comment.adapter.web.request.command.ModifyCommentWebRequest;
import com.dataracy.modules.comment.adapter.web.request.command.UploadCommentWebRequest;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.port.in.command.content.DeleteCommentUseCase;
import com.dataracy.modules.comment.application.port.in.command.content.ModifyCommentUseCase;
import com.dataracy.modules.comment.application.port.in.command.content.UploadCommentUseCase;
import com.dataracy.modules.comment.domain.status.CommentSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationCommentEdit;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class CommentCommandController implements CommentCommandApi {
    private final CommandCommentWebMapper commandCommentWebMapper;

    private final UploadCommentUseCase uploadCommentUseCase;
    private final ModifyCommentUseCase modifyCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;

    /**
     * 프로젝트에 새로운 댓글을 등록합니다.
     *
     * @param projectId 댓글이 등록될 프로젝트의 ID
     * @param userId 댓글을 작성하는 사용자의 ID
     * @param webRequest 댓글 등록 요청 정보
     * @return 댓글 등록 성공 시 201 Created 상태와 함께 성공 응답을 반환합니다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadComment(Long projectId, Long userId, UploadCommentWebRequest webRequest) {
        Instant startTime = LoggerFactory.api().logRequest("[UploadComment] 댓글 작성 API 요청 시작");

        try {
            UploadCommentRequest requestDto = commandCommentWebMapper.toApplicationDto(webRequest);
            uploadCommentUseCase.uploadComment(projectId, userId, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[UploadComment] 댓글 작성 API 응답 완료", startTime);
        }

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
    public ResponseEntity<SuccessResponse<Void>> modifyComment(Long projectId, Long commentId, ModifyCommentWebRequest webRequest) {
        Instant startTime = LoggerFactory.api().logRequest("[ModifyComment] 댓글 수정 API 요청 시작");

        try {
            ModifyCommentRequest requestDto = commandCommentWebMapper.toApplicationDto(webRequest);
            modifyCommentUseCase.modifyComment(projectId, commentId, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[ModifyComment] 댓글 수정 API 응답 완료", startTime);
        }

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
        Instant startTime = LoggerFactory.api().logRequest("[DeleteComment] 댓글 삭제 API 요청 시작");

        try {
            deleteCommentUseCase.deleteComment(projectId, commentId);
        } finally {
            LoggerFactory.api().logResponse("[DeleteComment] 댓글 삭제 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.DELETE_COMMENT));
    }
}
