package com.dataracy.modules.reference.application.port.in.authorlevel;

import java.util.List;
import java.util.Map;

public interface GetAuthorLevelLabelFromIdUseCase {
  /**
   * 주어진 저자 레벨 ID에 해당하는 레이블을 반환합니다.
   *
   * @param authorLevelId 저자 레벨의 고유 식별자
   * @return 해당 저자 레벨의 레이블 문자열
   */
  String getLabelById(Long authorLevelId);

  /**
   * 여러 저자 레벨 ID에 대해 각 ID에 해당하는 라벨 문자열을 반환합니다.
   *
   * @param authorLevelIds 라벨을 조회할 저자 레벨 ID 목록
   * @return 각 저자 레벨 ID와 해당 라벨 문자열의 매핑
   */
  Map<Long, String> getLabelsByIds(List<Long> authorLevelIds);
}
