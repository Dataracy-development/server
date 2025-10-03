/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.in.command.count;

public interface IncreaseLikeCountUseCase {
  /**
   * 지정된 프로젝트의 좋아요 개수를 1 증가시킵니다.
   *
   * @param projectId 좋아요 개수를 증가시킬 프로젝트의 ID
   */
  void increaseLikeCount(Long projectId);
}
