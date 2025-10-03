/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.port.out.validate;

public interface CheckDataExistsByIdPort {
  /**
   * 주어진 ID에 해당하는 데이터 엔티티가 저장소에 존재하는지 여부를 반환합니다.
   *
   * @param dataId 존재 여부를 확인할 데이터의 고유 식별자
   * @return 데이터가 존재하면 true, 존재하지 않으면 false
   */
  boolean existsDataById(Long dataId);
}
