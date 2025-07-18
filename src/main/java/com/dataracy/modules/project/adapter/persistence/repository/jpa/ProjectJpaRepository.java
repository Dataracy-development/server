package com.dataracy.modules.project.adapter.persistence.repository.jpa;

import com.dataracy.modules.project.adapter.persistence.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.persistence.repository.query.ProjectQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long>, ProjectQueryRepository {
}
