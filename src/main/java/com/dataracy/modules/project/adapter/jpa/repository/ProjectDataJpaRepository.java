package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProjectDataJpaRepository extends JpaRepository<ProjectDataEntity, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectDataEntity p WHERE p.project.id = :projectId")
    void deleteAllByProjectId(Long projectId);
}
