package com.dataracy.modules.user.application.port.in.query.extractor;

import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

public interface GetUserInfoUseCase {
    /**
 * 주어진 사용자 ID의 상세 사용자 정보를 추출하여 반환합니다.
 *
 * <p>사용자 조회에 필요한 도메인 수준의 정보를 구성해 UserInfo로 반환합니다.</p>
 *
 * @param userId 조회할 사용자의 고유 ID
 * @return 조회된 사용자의 정보를 담은 {@code UserInfo} 객체
 */
    UserInfo extractUserInfo(Long userId);

    /**
     * 지정된 사용자 ID로 해당 사용자의 정보를 조회하여 반환합니다.
     * 아이디를 라벨로 변환하여 반환한다.
     *
     * @param userId 정보를 조회할 사용자의 고유 ID
     * @return 조회된 사용자의 정보가 담긴 GetUserInfoResponse 객체
     */
    GetUserInfoResponse getUserInfo(Long userId);
}
