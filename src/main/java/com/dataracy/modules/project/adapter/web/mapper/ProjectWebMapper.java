package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import org.springframework.stereotype.Component;

@Component
public class ProjectWebMapper {
    /**
     * 웹 계층의 프로젝트 업로드 요청 DTO를 도메인 계층의 프로젝트 업로드 요청 DTO로 변환합니다.
     *
     * @param webRequest 변환할 프로젝트 업로드 웹 요청 DTO
     * @return 변환된 도메인 계층의 프로젝트 업로드 요청 DTO
     */
    public ProjectUploadRequest toApplicationDto(ProjectUploadWebRequest webRequest) {
        return new ProjectUploadRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId(),
                webRequest.isContinue(),
                webRequest.parentProjectId(),
                webRequest.content(),
                webRequest.dataIds()
        );
    }
}
