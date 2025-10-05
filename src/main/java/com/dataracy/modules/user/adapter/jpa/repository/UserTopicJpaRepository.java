package com.dataracy.modules.user.adapter.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dataracy.modules.user.adapter.jpa.entity.UserTopicEntity;

public interface UserTopicJpaRepository extends JpaRepository<UserTopicEntity, Long> {
  /**
   * 주어진 사용자 ID와 일치하는 모든 UserTopicEntity 엔티티를 삭제합니다.
   *
   * <p>지정한 userId를 가진 레코드를 모두 제거하며, 일치하는 레코드가 없으면 아무 작업도 수행하지 않습니다.
   *
   * @param userId 삭제 대상 사용자의 고유 식별자
   */
  void deleteAllByUserId(Long userId);
}
