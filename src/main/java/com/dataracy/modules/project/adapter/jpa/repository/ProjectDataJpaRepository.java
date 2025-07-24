package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectDataJpaRepository extends JpaRepository<ProjectDataEntity, Long> {
    List<ProjectDataEntity> findAllByProjectId(Long projectId);
}
