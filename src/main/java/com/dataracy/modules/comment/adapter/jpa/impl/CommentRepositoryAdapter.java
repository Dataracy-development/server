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

 /**
  * 댓글 도메인 객체를 JPA 엔티티로 변환하여 데이터베이스에 저장한 후, 저장된 댓글 도메인 객체를 반환합니다.
  *
  * @param comment 저장할 댓글 도메인 객체
  * @return 저장된 댓글 도메인 객체
  */
 @Override
 public Comment upload(Comment comment) {
  CommentEntity commentEntity = CommentEntityMapper.toEntity(comment);
  return CommentEntityMapper.toDomain(commentJpaRepository.save(commentEntity));
 }

 /**
  * 주어진 프로젝트 ID와 댓글 ID에 해당하는 댓글의 내용을 수정합니다.
  *
  * 댓글이 존재하지 않거나 프로젝트 ID가 일치하지 않을 경우 CommentException이 발생합니다.
  *
  * @param projectId 수정할 댓글이 속한 프로젝트의 ID
  * @param commentId 수정할 댓글의 ID
  * @param requestDto 수정할 내용을 담은 요청 객체
  * @throws CommentException 댓글이 존재하지 않거나 프로젝트와 댓글이 일치하지 않을 때 발생
  */
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

 /**
  * 주어진 프로젝트 ID와 댓글 ID를 기반으로 댓글을 삭제합니다.
  *
  * 프로젝트와 댓글의 연관성이 일치하지 않거나 댓글이 존재하지 않을 경우 예외가 발생합니다.
  *
  * @param projectId 삭제할 댓글이 속한 프로젝트의 ID
  * @param commentId 삭제할 댓글의 ID
  * @throws CommentException 댓글이 존재하지 않거나 프로젝트와 댓글의 연관성이 일치하지 않을 때 발생합니다.
  */
 @Override
 public void delete(Long projectId, Long commentId) {
  CommentEntity comment = commentJpaRepository.findById(commentId)
          .orElseThrow(() -> new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT));
  if (!comment.getProjectId().equals(projectId)) {
   throw new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
  }
  commentJpaRepository.delete(comment);
 }

  /**
   * 주어진 댓글 ID에 연결된 사용자 ID를 Optional로 반환합니다.
   *
   * @param commentId 사용자 ID를 조회할 댓글의 ID
   * @return 해당 댓글에 연결된 사용자 ID의 Optional, 존재하지 않으면 빈 Optional 반환
   */
  @Override
  public Optional<Long> findUserIdByCommentId(Long commentId) {
   return commentJpaRepository.findUserIdById(commentId);
  }

  /**
   * 주어진 댓글 ID에 해당하는 댓글이 데이터베이스에 존재하는지 여부를 반환합니다.
   *
   * @param commentId 존재 여부를 확인할 댓글의 ID
   * @return 댓글이 존재하면 true, 그렇지 않으면 false
   */
  @Override
  public boolean existsByCommentId(Long commentId) {
   return commentJpaRepository.existsById(commentId);
  }

  @Override
  public void increaseLikeCount(Long commentId) {
   commentJpaRepository.increaseLikeCount(commentId);
  }

  @Override
  public void decreaseLikeCount(Long commentId) {
   commentJpaRepository.decreaseLikeCount(commentId);
  }
 }
