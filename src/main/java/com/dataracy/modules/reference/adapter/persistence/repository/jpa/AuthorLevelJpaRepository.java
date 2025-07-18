package com.dataracy.modules.reference.adapter.persistence.repository.jpa;

import com.dataracy.modules.reference.adapter.persistence.entity.AuthorLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorLevelJpaRepository extends JpaRepository<AuthorLevelEntity, Long> {
}
