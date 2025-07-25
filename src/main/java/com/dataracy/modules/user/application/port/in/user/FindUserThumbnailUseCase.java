package com.dataracy.modules.user.application.port.in.user;

import java.util.List;
import java.util.Map;

public interface FindUserThumbnailUseCase {
/**
 * 주어진 사용자 ID 목록에 대해 각 사용자의 썸네일 정보를 반환합니다.
 *
 * @param userIds 썸네일을 조회할 사용자 ID 목록
 * @return 각 사용자 ID와 해당 사용자의 썸네일 문자열이 매핑된 Map
 */
Map<Long, String> findUserThumbnailsByIds(List<Long> userIds);
}
