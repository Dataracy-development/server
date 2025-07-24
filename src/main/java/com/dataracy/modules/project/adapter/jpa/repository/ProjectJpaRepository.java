package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    @Query(value = "SELECT * FROM project WHERE project_id = :projectId", nativeQuery = true) // @Where 무시됨
    Optional<ProjectEntity> findIncludingDeleted(@Param("projectId") Long projectId);
}
