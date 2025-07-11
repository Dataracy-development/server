package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import org.springframework.stereotype.Component;

@Component
public class ProjectWebMapper {
    // 프로젝트 업로드 웹 요청 DTO -> 프로젝트 업로드 도메인 요청 DTO
    public ProjectUploadRequest toApplicationDto(ProjectUploadWebRequest webRequest) {
        return new ProjectUploadRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId(),
                webRequest.isContinue(),
                webRequest.parentProjectId(),
                webRequest.content()
        );
    }
}
