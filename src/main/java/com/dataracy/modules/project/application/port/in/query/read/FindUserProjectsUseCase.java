package com.dataracy.modules.project.application.port.in.query.read;

import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserProjectsUseCase {
    Page<UserProjectResponse> findUserProjects(Long userId, Pageable pageable);
    Page<UserProjectResponse> findLikeProjects(Long userId, Pageable pageable);
}
