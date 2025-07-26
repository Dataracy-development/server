package com.dataracy.modules.comment.application.port.in;

import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindCommentListUseCase {
    /**
 * 지정된 프로젝트의 댓글 목록을 페이지 단위로 조회합니다.
 *
 * @param projectId 댓글을 조회할 프로젝트의 식별자
 * @param pageable 페이지네이션 정보를 담은 객체
 * @return 프로젝트에 속한 댓글의 페이지별 목록
 */
Page<FindCommentResponse> findComments(Long projectId, Pageable pageable);
}
