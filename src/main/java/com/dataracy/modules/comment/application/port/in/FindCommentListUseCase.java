package com.dataracy.modules.comment.application.port.in;

import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindCommentListUseCase {
    Page<FindCommentResponse> findComments(Long projectId, Pageable pageable);
}
