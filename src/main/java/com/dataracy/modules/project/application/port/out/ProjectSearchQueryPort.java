package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.domain.model.ProjectSearchResult;

import java.util.List;

public interface ProjectSearchQueryPort {
    List<ProjectSearchResult> searchProjectByTitleOrUsername(String keyword);
}
