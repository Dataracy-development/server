package com.dataracy.modules.user.application.port.out.jpa;

import java.util.List;
import java.util.Map;

/**
 * user db 다건 조회 포트
 */
public interface UserMultiQueryPort {
    /**
     * 주어진 사용자 ID 목록에 대해 각 사용자 ID와 해당 사용자의 닉네임을 매핑한 Map을 반환합니다.
     *
     * @param userIds 닉네임을 조회할 사용자 ID 목록
     * @return 각 사용자 ID와 해당 닉네임이 매핑된 Map
     */
    Map<Long, String> findUsernamesByIds(List<Long> userIds);

    /**
     * 주어진 사용자 ID 목록에 대해 각 사용자 ID와 해당 사용자의 썸네일 url을 매핑하여 반환합니다.
     *
     * @param userIds 썸네일 정보를 조회할 사용자 ID 목록
     * @return 각 사용자 ID와 해당 썸네일 url이 매핑된 Map
     */
    Map<Long, String> findUserThumbnailsByIds(List<Long> userIds);

    /**
     * 주어진 사용자 ID 목록에 대해 각 사용자 ID와 해당 사용자의 작성자 유형 ID를 매핑한 Map을 반환합니다.
     *
     * @param userIds 작성자 유형 ID를 조회할 사용자 ID 목록
     * @return 각 사용자 ID와 해당 작성자 유형 ID(String)로 구성된 Map
     */
    Map<Long, String> findUserAuthorLevelIds(List<Long> userIds);
}
