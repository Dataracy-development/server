package com.dataracy.modules.comment.adapter.jpa.repository;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    /**
     * 주어진 댓글 ID에 해당하는 사용자의 ID를 조회합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글 작성자의 사용자 ID를 포함하는 Optional 객체. 해당 댓글이 없으면 빈 Optional을 반환합니다.
     */
    @Query("select c.userId from CommentEntity c where c.id = :commentId")
    Optional<Long> findUserIdById(@Param("commentId") Long commentId);
}
