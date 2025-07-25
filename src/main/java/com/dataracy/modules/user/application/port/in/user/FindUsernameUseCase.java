package com.dataracy.modules.user.application.port.in.user;

import java.util.List;
import java.util.Map;

public interface FindUsernameUseCase {
    /**
 * 주어진 사용자 ID에 해당하는 사용자 이름을 반환합니다.
 *
 * @param userId 조회할 사용자의 고유 ID
 * @return 해당 ID에 연결된 사용자 이름
 */
String findUsernameById(Long userId);
    /**
 * 여러 사용자 ID에 대해 각 사용자 ID와 해당 사용자 이름을 매핑한 Map을 반환합니다.
 *
 * @param userIds 사용자 ID 목록
 * @return 각 사용자 ID에 해당하는 사용자 이름이 매핑된 Map
 */
Map<Long, String> findUsernamesByIds(List<Long> userIds);
}
