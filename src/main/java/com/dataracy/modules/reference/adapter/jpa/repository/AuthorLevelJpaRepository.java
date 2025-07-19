package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorLevelJpaRepository extends JpaRepository<AuthorLevelEntity, Long> {
    Optional<String> findLabelById(Long id);
}
