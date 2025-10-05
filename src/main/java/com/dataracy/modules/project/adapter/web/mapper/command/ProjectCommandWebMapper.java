package com.dataracy.modules.project.adapter.web.mapper.command;

import org.springframework.stereotype.Component;

import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.command.UploadProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.dto.response.command.UploadProjectResponse;

@Component
public class ProjectCommandWebMapper {
  /**
   * 웹 계층의 프로젝트 업로드 요청 DTO를 애플리케이션 계층의 업로드 요청 DTO로 변환합니다.
   *
   * @param webRequest 변환할 프로젝트 업로드 웹 요청 DTO
   * @return 변환된 애플리케이션 계층의 프로젝트 업로드 요청 DTO
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
        webRequest.dataIds());
  }

  /**
   * 애플리케이션 계층의 프로젝트 업로드 응답을 웹 계층 응답 DTO로 변환합니다.
   *
   * @param responseDto 애플리케이션 계층의 UploadProjectResponse — 생성된 프로젝트의 식별자(id)를 포함합니다.
   * @return 웹 계층의 UploadProjectWebResponse 객체(생성된 프로젝트의 id 포함)
   */
  public UploadProjectWebResponse toWebDto(UploadProjectResponse responseDto) {
    return new UploadProjectWebResponse(responseDto.id());
  }

  /**
   * 웹 계층의 프로젝트 수정 요청 DTO를 애플리케이션 계층의 프로젝트 수정 요청 DTO로 변환합니다.
   *
   * @param webRequest 프로젝트 수정 정보를 담은 웹 요청 DTO
   * @return 애플리케이션 계층에서 사용하는 프로젝트 수정 요청 DTO
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
        webRequest.dataIds());
  }
}
