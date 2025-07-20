package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.request.ProjectFilterWebRequest;
import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import org.springframework.stereotype.Component;

@Component
public class ProjectFilterWebMapper {
    public ProjectFilterRequest toApplicationDto(ProjectFilterWebRequest webRequest) {
        return new ProjectFilterRequest(
                webRequest.keyword(),
                webRequest.sortType(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId()
        );
    }
}
