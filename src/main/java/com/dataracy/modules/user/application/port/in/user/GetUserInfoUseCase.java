package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

public interface GetUserInfoUseCase {
    /**
 * 주어진 사용자 ID에 해당하는 사용자 정보를 반환합니다.
 *
 * @param userId 조회할 사용자의 ID
 * @return 사용자 정보 객체
 */
UserInfo getUserInfo(Long userId);
}
