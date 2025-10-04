package com.dataracy.modules.project.application.port.in.command.count;

public interface DecreaseLikeCountUseCase {
  /**
   * 지정된 프로젝트의 좋아요 수를 1만큼 감소시킵니다.
   *
   * @param projectId 좋아요 수를 감소시킬 대상 프로젝트의 식별자
   */
  void decreaseLikeCount(Long projectId);
}
