package com.dataracy.modules.project.adapter.persistence.repository.query;

import com.dataracy.modules.project.domain.model.Project;

import java.util.Optional;

public interface ProjectQueryRepository {
    Optional<Project> findProjectById(Long projectId);
}
