package com.dataracy.modules.comment.adapter.jpa.repository;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
}
