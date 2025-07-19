package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitSourceJpaRepository extends JpaRepository<VisitSourceEntity, Long> {
    Optional<String> findLabelById(Long id);
}
