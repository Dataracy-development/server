package com.dataracy.modules.user.adapter.persistence.repository.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.AuthorLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorLevelJpaRepository extends JpaRepository<AuthorLevelEntity, Long> {
}
