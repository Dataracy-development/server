package com.dataracy.modules.reference.adapter.persistence.repository;

import com.dataracy.modules.reference.adapter.persistence.entity.VisitSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitSourceJpaRepository extends JpaRepository<VisitSourceEntity, Long> {
}
