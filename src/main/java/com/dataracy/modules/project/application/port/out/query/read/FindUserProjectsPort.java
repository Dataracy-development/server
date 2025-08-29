package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserProjectsPort {
    Page<Project> findUserProjects(Long userId, Pageable pageable);
}
