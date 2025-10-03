/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.in.storage;

public interface UpdatePopularProjectsStorageUseCase {
  /**
   * 저장소에 데이터가 없을 때 즉시 업데이트합니다.
   *
   * @param size 조회할 프로젝트 개수
   */
  void warmUpCacheIfNeeded(int size);
}
