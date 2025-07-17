package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.port.in.ProjectRealTimeSearchUseCase;
import com.dataracy.modules.project.application.port.query.ProjectSearchQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryService implements ProjectRealTimeSearchUseCase {
    private final ProjectSearchQueryPort projectSearchQueryPort;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectRealTimeSearchResponse> search(String keyword, int size) {
        return projectSearchQueryPort.search(keyword, size);
    }
}
