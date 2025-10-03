/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.out.command.update;

public interface UpdateProjectCommentPort {
  /**
   * 지정된 프로젝트의 댓글 수를 1 증가시킵니다.
   *
   * @param projectId 댓글 수를 증가시킬 프로젝트의 ID
   */
  void increaseCommentCount(Long projectId);

  /**
   * 지정된 프로젝트의 댓글 수를 1 감소시킵니다.
   *
   * @param projectId 댓글 수를 감소시킬 프로젝트의 ID
   */
  void decreaseCommentCount(Long projectId);
}
