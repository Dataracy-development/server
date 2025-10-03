/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.port.out.query.read;

import java.util.List;

import com.dataracy.modules.dataset.application.dto.response.read.DataGroupCountResponse;

public interface GetDataGroupCountPort {
  /**
   * 데이터셋을 그룹화 기준에 따라 집계하여 각 그룹별 데이터셋 개수를 반환합니다.
   *
   * @return 각 그룹별 데이터셋 개수 정보를 담은 DataGroupCountResponse 객체의 리스트
   */
  List<DataGroupCountResponse> getDataGroupCount();
}
