 package com.dataracy.modules.comment.adapter.jpa.impl;

 import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
 import com.dataracy.modules.comment.adapter.jpa.mapper.CommentEntityMapper;
 import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
 import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
 import com.dataracy.modules.comment.application.port.out.CommentRepositoryPort;
 import com.dataracy.modules.comment.domain.exception.CommentException;
 import com.dataracy.modules.comment.domain.model.Comment;
 import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Repository;

 import java.util.Optional;

 @Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepositoryPort {
 private final CommentJpaRepository commentJpaRepository;

 @Override
 public void upload(Comment comment) {
  CommentEntity commentEntity = CommentEntityMapper.toEntity(comment);
  commentJpaRepository.save(commentEntity);
 }

 @Override
 public void modify(Long projectId, Long commentId, CommentModifyRequest requestDto) {
    CommentEntity comment = commentJpaRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT));
    if (!comment.getProjectId().equals(projectId)) {
     throw new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
    comment.modifyContent(requestDto.content());
    commentJpaRepository.save(comment);
 }

 @Override
 public void delete(Long projectId, Long commentId) {
  CommentEntity comment = commentJpaRepository.findById(commentId)
          .orElseThrow(() -> new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT));
  if (!comment.getProjectId().equals(projectId)) {
   throw new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
  }
  commentJpaRepository.delete(comment);
 }

  @Override
  public Optional<Long> findUserIdByCommentId(Long commentId) {
   return commentJpaRepository.findUserIdById(commentId);
  }
 }
