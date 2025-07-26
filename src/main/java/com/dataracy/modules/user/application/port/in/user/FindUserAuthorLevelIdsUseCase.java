package com.dataracy.modules.user.application.port.in.user;

import java.util.List;
import java.util.Map;

public interface FindUserAuthorLevelIdsUseCase {
/**
 * 주어진 사용자 ID 목록에 대해 각 사용자의 저자 레벨 ID를 조회하여 반환합니다.
 *
 * @param userIds 조회할 사용자 ID 목록
 * @return 각 사용자 ID와 해당 저자 레벨 ID가 매핑된 Map
 */
Map<Long, String> findUserAuthorLevelIds(List<Long> userIds);
}
