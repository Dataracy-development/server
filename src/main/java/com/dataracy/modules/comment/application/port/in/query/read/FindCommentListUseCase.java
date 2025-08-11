package com.dataracy.modules.comment.application.port.in.query.read;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindCommentListUseCase {
    /**
     * 특정 프로젝트에 작성된 댓글 목록을 페이지 단위로 조회합니다.
     * 해당 유저가 댓글을 좋아요 했는지 여부를 함께 판단한다.
     *
     * @param userId   댓글을 조회하는 사용자의 식별자
     * @param projectId 댓글을 조회할 프로젝트의 식별자
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 프로젝트에 속한 댓글의 페이지별 목록
     */
    Page<FindCommentResponse> findComments(Long userId, Long projectId, Pageable pageable);
}
