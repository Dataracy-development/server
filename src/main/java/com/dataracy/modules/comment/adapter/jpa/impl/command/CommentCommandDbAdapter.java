package com.dataracy.modules.comment.adapter.jpa.impl.command;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.adapter.jpa.mapper.CommentEntityMapper;
import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.port.out.command.create.UploadCommentPort;
import com.dataracy.modules.comment.application.port.out.command.delete.DeleteCommentPort;
import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentCommandDbAdapter
    implements UploadCommentPort, UpdateCommentPort, DeleteCommentPort {
  private final CommentJpaRepository commentJpaRepository;

  // Entity 및 메시지 상수 정의
  private static final String COMMENT_ENTITY = "CommentEntity";
  private static final String COMMENT_NOT_FOUND_MESSAGE = "해당 댓글이 존재하지 않습니다. commentId=";

  /**
   * 댓글 도메인 객체를 데이터베이스에 저장하고, 저장된 댓글 도메인 객체를 반환합니다.
   *
   * @param comment 저장할 댓글 도메인 객체
   * @return 데이터베이스에 저장된 댓글 도메인 객체
   */
  @Override
  public Comment uploadComment(Comment comment) {
    CommentEntity commentEntity = CommentEntityMapper.toEntity(comment);
    Comment savedComment = CommentEntityMapper.toDomain(commentJpaRepository.save(commentEntity));
    LoggerFactory.db()
        .logSave(COMMENT_ENTITY, String.valueOf(savedComment.getId()), "댓글 작성이 완료되었습니다.");
    return savedComment;
  }

  /**
   * 지정한 프로젝트 ID와 댓글 ID에 해당하는 댓글의 내용을 요청 정보로 수정합니다. 댓글이 존재하지 않거나 프로젝트 ID와 댓글의 프로젝트 ID가 일치하지 않을 경우
   * CommentException이 발생합니다.
   *
   * @param projectId 댓글이 속한 프로젝트의 ID
   * @param commentId 수정할 댓글의 ID
   * @param requestDto 수정할 내용을 담은 요청 객체
   * @throws CommentException 댓글이 존재하지 않거나 프로젝트와 댓글이 일치하지 않을 때 발생
   */
  @Override
  public void modifyComment(Long projectId, Long commentId, ModifyCommentRequest requestDto) {
    CommentEntity comment =
        commentJpaRepository
            .findById(commentId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(COMMENT_ENTITY, COMMENT_NOT_FOUND_MESSAGE + commentId);
                  return new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);
                });
    if (!comment.getProjectId().equals(projectId)) {
      LoggerFactory.db()
          .logWarning(
              COMMENT_ENTITY,
              "해당 프로젝트에 작성된 댓글이 아닙니다. projectId=" + projectId + ", commentId=" + commentId);
      throw new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
    comment.modifyContent(requestDto.content());
    commentJpaRepository.save(comment);
    LoggerFactory.db().logUpdate(COMMENT_ENTITY, String.valueOf(commentId), "댓글 업데이트가 완료되었습니다.");
  }

  /**
   * 프로젝트 ID와 댓글 ID를 기준으로 해당 댓글을 삭제합니다. 댓글이 존재하지 않거나, 댓글이 지정된 프로젝트에 속하지 않을 경우 `CommentException`이
   * 발생합니다.
   *
   * @param projectId 댓글이 속한 프로젝트의 ID
   * @param commentId 삭제할 댓글의 ID
   * @throws CommentException 댓글이 존재하지 않거나 프로젝트와 댓글의 연관성이 일치하지 않을 때 발생합니다.
   */
  @Override
  public void deleteComment(Long projectId, Long commentId) {
    CommentEntity comment =
        commentJpaRepository
            .findById(commentId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(COMMENT_ENTITY, COMMENT_NOT_FOUND_MESSAGE + commentId);
                  return new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);
                });
    if (!comment.getProjectId().equals(projectId)) {
      LoggerFactory.db()
          .logWarning(
              COMMENT_ENTITY,
              "해당 프로젝트에 작성된 댓글이 아닙니다. projectId=" + projectId + ", commentId=" + commentId);
      throw new CommentException(CommentErrorStatus.MISMATCH_PROJECT_COMMENT);
    }
    commentJpaRepository.delete(comment);
    LoggerFactory.db().logDelete(COMMENT_ENTITY, String.valueOf(commentId), "댓글 삭제가 완료되었습니다.");
  }
}
