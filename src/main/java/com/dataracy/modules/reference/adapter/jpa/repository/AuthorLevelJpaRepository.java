package com.dataracy.modules.reference.adapter.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;

public interface AuthorLevelJpaRepository extends JpaRepository<AuthorLevelEntity, Long> {
  /**
   * 주어진 ID에 해당하는 AuthorLevelEntity의 라벨을 조회하여 Optional로 반환합니다.
   *
   * @param id 조회할 AuthorLevelEntity의 ID
   * @return 라벨이 존재하면 Optional로 감싸서 반환하며, 없으면 빈 Optional을 반환합니다.
   */
  @Query("SELECT a.label FROM AuthorLevelEntity a WHERE a.id = :id")
  Optional<String> findLabelById(Long id);
}
