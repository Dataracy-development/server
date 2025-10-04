package com.dataracy.modules.project.application.port.out.command.update;

import java.util.Map;

public interface UpdateProjectViewPort {
  /**
   * 지정된 프로젝트의 조회수를 특정 값만큼 증가시킵니다.
   *
   * @param projectId 조회수를 증가시킬 프로젝트의 ID
   * @param increment 증가시킬 조회수 값
   */
  void increaseViewCount(Long projectId, Long increment);

  /**
   * 여러 프로젝트의 조회수를 배치로 증가시킵니다.
   *
   * @param viewCountUpdates 프로젝트 ID와 증가시킬 조회수 값의 맵
   */
  void increaseViewCountBatch(Map<Long, Long> viewCountUpdates);
}
