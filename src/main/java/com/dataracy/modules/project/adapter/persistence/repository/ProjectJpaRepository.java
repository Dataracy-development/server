package com.dataracy.modules.project.adapter.persistence.repository;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
}
