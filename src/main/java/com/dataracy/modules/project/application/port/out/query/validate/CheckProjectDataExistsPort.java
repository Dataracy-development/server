/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.out.query.validate;

public interface CheckProjectDataExistsPort {
  /**
   * 주어진 프로젝트 ID에 해당하는 프로젝트 데이터가 존재하는지 여부를 반환합니다.
   *
   * @param projectId 존재 여부를 확인할 프로젝트의 ID
   * @return 프로젝트 데이터가 존재하면 true, 존재하지 않으면 false
   */
  boolean checkProjectDataExistsByProjectId(Long projectId);
}
