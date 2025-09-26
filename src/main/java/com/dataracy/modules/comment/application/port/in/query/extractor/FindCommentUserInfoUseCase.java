package com.dataracy.modules.comment.application.port.in.query.extractor;

import com.dataracy.modules.comment.application.dto.response.support.CommentLabelResponse;

import java.util.List;

/**
 * 댓글 조회 시 필요한 사용자 정보를 배치로 조회하는 UseCase
 */
public interface FindCommentUserInfoUseCase {
    
    /**
     * 주어진 사용자 ID 목록에 대해 댓글 표시에 필요한 사용자 정보를 배치로 조회합니다.
     * 
     * @param userIds 정보 조회 대상인 사용자 ID 목록
     * @return 각 사용자 ID에 대한 사용자명, 사용자 프로필 이미지 URL, 작성자 레벨 ID, 작성자 레벨 라벨이 포함된 CommentLabelResponse 객체
     */
    CommentLabelResponse findCommentUserInfoBatch(List<Long> userIds);
}
