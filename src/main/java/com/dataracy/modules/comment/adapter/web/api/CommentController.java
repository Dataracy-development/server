package com.dataracy.modules.comment.adapter.web.api;

import com.dataracy.modules.comment.adapter.web.mapper.CommentWebMapper;
import com.dataracy.modules.comment.adapter.web.request.CommentModifyWebRequest;
import com.dataracy.modules.comment.adapter.web.request.CommentUploadWebRequest;
import com.dataracy.modules.comment.adapter.web.response.FindCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;
import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.application.mapper.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.in.CommentDeleteUseCase;
import com.dataracy.modules.comment.application.port.in.CommentModifyUseCase;
import com.dataracy.modules.comment.application.port.in.CommentUploadUseCase;
import com.dataracy.modules.comment.application.port.in.FindCommentListUseCase;
import com.dataracy.modules.comment.domain.status.CommentSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.response.ConnectedDataAssociatedWithProjectWebResponse;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentWebMapper commentWebMapper;
    private final FindCommentDtoMapper findCommentDtoMapper;

    private final CommentUploadUseCase commentUploadUseCase;
    private final CommentModifyUseCase commentModifyUseCase;
    private final CommentDeleteUseCase commentDeleteUseCase;
    private final FindCommentListUseCase findCommentListUseCase;

    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadComment(Long projectId, Long userId, CommentUploadWebRequest webRequest) {
        CommentUploadRequest requestDto = commentWebMapper.toApplicationDto(webRequest);
        commentUploadUseCase.upload(projectId, userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(CommentSuccessStatus.CREATED_COMMENT));
    }

    @Override
    public ResponseEntity<SuccessResponse<Void>> modifyComment(Long projectId, Long commentId, Long userId, CommentModifyWebRequest webRequest) {
        CommentModifyRequest requestDto = commentWebMapper.toApplicationDto(webRequest);
        commentModifyUseCase.modify(projectId, commentId, userId, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.MODIFY_COMMENT));
    }

    @Override
    public ResponseEntity<SuccessResponse<Void>> deleteComment(Long projectId, Long commentId) {
        commentDeleteUseCase.delete(projectId, commentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.DELETE_COMMENT));
    }

    @Override
    public ResponseEntity<SuccessResponse<Page<FindCommentWebResponse>>> findComments(Long projectId, Pageable pageable) {
        Page<FindCommentResponse> responseDto = findCommentListUseCase.findComments(projectId, pageable);
        Page<FindCommentWebResponse> webResponse = responseDto.map(commentWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(CommentSuccessStatus.GET_COMMENTS, webResponse));
    }
}
