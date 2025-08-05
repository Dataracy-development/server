package com.dataracy.modules.user.application.port.in.query.extractor;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

public interface GetUserInfoUseCase {
    /**
 * 지정된 사용자 ID로 해당 사용자의 정보를 조회합니다.
 *
 * @param userId 정보를 조회할 사용자의 고유 ID
 * @return 조회된 사용자의 정보가 담긴 UserInfo 객체
 */
    UserInfo getUserInfo(Long userId);
}
