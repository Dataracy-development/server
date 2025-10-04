package com.dataracy.modules.user.application.port.in.query.extractor;

import java.util.List;
import java.util.Map;

public interface FindUserThumbnailUseCase {

  /**
   * 지정한 사용자 ID에 대한 썸네일 문자열을 조회한다.
   *
   * @param userId 조회할 사용자 식별자
   * @return 조회된 썸네일 문자열 (해당 사용자의 썸네일이 없으면 구현체에 따라 null 또는 빈 값을 반환할 수 있음)
   */
  String findUserThumbnailById(Long userId);

  /**
   * 주어진 사용자 ID 목록에 대해 각 사용자의 썸네일 URL을 조회하여 반환합니다.
   *
   * @param userIds 썸네일 정보를 조회할 사용자 ID 목록
   * @return 각 사용자 ID와 해당 사용자의 썸네일 문자열이 매핑된 Map
   */
  Map<Long, String> findUserThumbnailsByIds(List<Long> userIds);
}
