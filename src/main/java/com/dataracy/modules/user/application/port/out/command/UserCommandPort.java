package com.dataracy.modules.user.application.port.out.command;

import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.domain.model.User;

public interface UserCommandPort {
    /**
     * 사용자를 데이터베이스에 저장하고, 저장된 사용자 도메인 모델을 반환합니다.
     *
     * @param user 저장 또는 갱신할 사용자 도메인 모델
     * @return 저장된 사용자 도메인 모델
     */
    User saveUser(User user);

    /**
 * 지정한 사용자(ID)의 비밀번호를 인코딩된 값으로 갱신합니다.
 *
 * 영속 계층에 변경된 비밀번호를 저장하여 이후 인증 시 새로운 비밀번호가 사용되도록 합니다.
 *
 * @param userId 비밀번호를 변경할 대상 사용자의 고유 식별자
 * @param encodePassword 저장할 인코딩된 비밀번호
 */
    void changePassword(Long userId, String encodePassword);

    /**
 * 사용자 정보를 수정합니다.
 *
 * 지정한 사용자 ID의 회원 정보를 전달된 ModifyUserInfoRequest의 내용으로 업데이트합니다.
 *
 * @param userId 수정할 대상 사용자의 고유 식별자
 * @param requestDto 수정할 사용자 정보(이름, 연락처 등 변경 가능한 필드 포함)
 */
void modifyUserInfo(Long userId, ModifyUserInfoRequest requestDto);

    /**
 * 지정한 사용자의 프로필 이미지 파일 URL을 업데이트한다.
 *
 * @param userId              업데이트 대상 사용자의 식별자
 * @param profileImageFileUrl 새 프로필 이미지 파일의 URL
 */
void updateProfileImageFile(Long userId, String profileImageFileUrl);

    /**
 * 지정한 사용자를 탈퇴 처리한다.
 *
 * 구현체에 따라 계정 비활성화 또는 삭제 등 탈퇴에 필요한 모든 변경을 수행한다.
 *
 * @param userId 탈퇴 처리할 사용자의 식별자
 */
void withdrawalUser(Long userId);
}
