package com.dataracy.modules.comment.adapter.jpa.repository;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    @Query("select c.userId from CommentEntity c where c.id = :commentId")
    Optional<Long> findUserIdById(@Param("commentId") Long commentId);
}
