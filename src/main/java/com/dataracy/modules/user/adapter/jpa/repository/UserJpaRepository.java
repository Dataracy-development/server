package com.dataracy.modules.user.adapter.jpa.repository;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    /**
     * 주어진 providerId로 사용자를 조회합니다.
     *
     * @param providerId 외부 인증 제공자의 고유 식별자
     * @return 해당 providerId를 가진 사용자가 있으면 Optional<UserEntity>, 없으면 Optional.empty()
     */
    Optional<UserEntity> findByProviderId(String providerId);

    /**
     * 주어진 이메일 주소로 사용자 엔티티를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소
     * @return 해당 이메일 주소를 가진 사용자가 존재하면 Optional<UserEntity>, 없으면 빈 Optional
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 지정한 이메일 주소를 가진 사용자가 데이터베이스에 존재하는지 여부를 반환합니다.
     *
     * @param email 존재 여부를 확인할 이메일 주소
     * @return 사용자가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);

    /**
     * 주어진 닉네임을 가진 사용자가 데이터베이스에 존재하는지 여부를 반환합니다.
     *
     * @param nickname 존재 여부를 확인할 사용자 닉네임
     * @return 사용자가 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByNickname(String nickname);

    /**
     * 지정된 사용자 ID에 해당하는 사용자를 탈퇴 처리하여 삭제 상태로 표시합니다.
     *
     * @param userId 탈퇴 처리할 사용자의 ID
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isDeleted = true WHERE u.id = :userId")
    void withdrawalUser(Long userId);

    Optional<String> findNicknameById(Long id);
}
