/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.mapper.support;

import org.springframework.stereotype.Component;

import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;

@Component
public class ProjectConnectedDataWebMapper {
  /**
   * ProjectConnectedDataResponse를 ProjectConnectedDataWebResponse로 변환합니다.
   *
   * <p>응답 DTO의 각 필드(id, title, creatorId, creatorName, userProfileImageUrl, topicLabel,
   * dataTypeLabel, startDate, endDate, dataThumbnailUrl, downloadCount, rowCount, columnCount,
   * createdAt, countConnectedProjects)를 대응하는 웹 응답 DTO 필드로 그대로 매핑하여 새 객체를 생성합니다.
   *
   * @param responseDto 변환할 소스 DTO. null일 경우 내부 필드 접근으로 인해 NullPointerException이 발생할 수 있습니다.
   * @return 변환된 ProjectConnectedDataWebResponse 인스턴스
   */
  public ProjectConnectedDataWebResponse toWebDto(ProjectConnectedDataResponse responseDto) {
    return new ProjectConnectedDataWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.topicLabel(),
        responseDto.dataTypeLabel(),
        responseDto.startDate(),
        responseDto.endDate(),
        responseDto.dataThumbnailUrl(),
        responseDto.downloadCount(),
        responseDto.rowCount(),
        responseDto.columnCount(),
        responseDto.createdAt(),
        responseDto.countConnectedProjects());
  }
}
