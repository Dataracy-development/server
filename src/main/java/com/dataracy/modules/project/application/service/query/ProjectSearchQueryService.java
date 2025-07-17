package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.port.out.ProjectSearchQueryPort;
import com.dataracy.modules.project.domain.model.ProjectSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectSearchQueryService {
    private final ProjectSearchQueryPort projectSearchQueryPort;

    public List<ProjectSearchResult> search(String keyword) {
        return projectSearchQueryPort.searchProjectByTitleOrUsername(keyword);
    }
}
