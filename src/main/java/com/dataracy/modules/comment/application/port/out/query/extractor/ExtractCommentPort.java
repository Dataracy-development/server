package com.dataracy.modules.comment.application.port.out.query.extractor;

import java.util.Optional;

public interface ExtractCommentPort {
    /**
     * 주어진 댓글 ID에 해당하는 댓글 작성자의 사용자 ID를 반환합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글 작성자의 사용자 ID가 존재하면 해당 값을 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<Long> findUserIdByCommentId(Long commentId);
}
