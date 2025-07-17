package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.response.ProjectRealTimeSearchWebResponse;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectSearchWebMapper {
    public ProjectRealTimeSearchWebResponse toWeb(ProjectRealTimeSearchResponse responseDto) {
        return new ProjectRealTimeSearchWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.fileUrl()
        );
    }
}
