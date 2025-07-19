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
    List<Long> findUsernamesByIds();
    Map<Long, String> findUsernamesByIds(List<Long> userIds);
}
