package com.dataracy.modules.project.adapter.persistence.repository;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    @Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.parentProject WHERE p.id = :id")
    Optional<ProjectEntity> findProjectById(@Param("id") Long id);
}
