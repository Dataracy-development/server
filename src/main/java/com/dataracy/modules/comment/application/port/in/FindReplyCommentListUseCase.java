package com.dataracy.modules.comment.application.port.in;

import com.dataracy.modules.comment.application.dto.response.FindReplyCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindReplyCommentListUseCase {
    Page<FindReplyCommentResponse> findReplyComments(Long projectId, Long commentId, Pageable pageable);
}
