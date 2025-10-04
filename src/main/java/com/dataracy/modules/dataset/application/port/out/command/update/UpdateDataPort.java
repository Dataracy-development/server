package com.dataracy.modules.dataset.application.port.out.command.update;

import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;

public interface UpdateDataPort {
  /**
   * 주어진 데이터 ID에 해당하는 데이터 엔터티를 `ModifyDataRequest`의 정보로 수정합니다.
   *
   * @param dataId 수정 대상 데이터의 고유 식별자
   * @param requestDto 데이터 수정에 필요한 정보가 담긴 요청 객체
   */
  void modifyData(Long dataId, ModifyDataRequest requestDto);
}
