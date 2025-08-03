package com.dataracy.modules.project.adapter.web.mapper.command;

import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import org.springframework.stereotype.Component;

@Component
public class ProjectCommandWebMapper {
    /**
     * 웹 계층의 프로젝트 업로드 요청 DTO를 애플리케이션 계층의 프로젝트 업로드 요청 DTO로 변환합니다.
     *
     * @param webRequest 변환할 프로젝트 업로드 웹 요청 DTO
     * @return 애플리케이션 계층에서 사용할 프로젝트 업로드 요청 DTO
     */
    public UploadProjectRequest toApplicationDto(UploadProjectWebRequest webRequest) {
        return new UploadProjectRequest(
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

    /**
     * 웹 계층의 프로젝트 수정 요청 DTO를 애플리케이션 계층의 프로젝트 수정 요청 DTO로 변환합니다.
     *
     * @param webRequest 프로젝트 수정 정보를 담은 웹 요청 DTO
     * @return 변환된 애플리케이션 계층의 프로젝트 수정 요청 DTO
     */
    public ModifyProjectRequest toApplicationDto(ModifyProjectWebRequest webRequest) {
        return new ModifyProjectRequest(
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
