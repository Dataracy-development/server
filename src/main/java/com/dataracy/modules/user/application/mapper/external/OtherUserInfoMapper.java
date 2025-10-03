/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.mapper.external;

import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;

@Component
public class OtherUserInfoMapper {

  /**
   * 내부의 UserProjectResponse를 외부 API용 GetOtherUserProjectResponse로 변환한다.
   *
   * <p>다음 필드를 1:1 매핑하여 새 응답 DTO를 생성한다: id, title, content, projectThumbnailUrl, topicLabel,
   * authorLevelLabel, commentCount, likeCount, viewCount, createdAt.
   *
   * @param source 변환할 내부 표현의 UserProjectResponse
   * @return 외부 API에 반환할 GetOtherUserProjectResponse 인스턴스
   */
  public GetOtherUserProjectResponse toOtherUserProject(UserProjectResponse source) {
    return new GetOtherUserProjectResponse(
        source.id(),
        source.title(),
        source.content(),
        source.projectThumbnailUrl(),
        source.topicLabel(),
        source.authorLevelLabel(),
        source.commentCount(),
        source.likeCount(),
        source.viewCount(),
        source.createdAt());
  }

  /**
   * UserDataResponse를 GetOtherUserDataResponse로 매핑하여 다른 사용자의 데이터 정보를 반환합니다.
   *
   * <p>변환 시 source의 식별자, 제목, 토픽·데이터 타입 레이블, 기간(start/end), 썸네일 URL, 다운로드 수, 크기(바이트), 행·열 수, 생성일,
   * 연결된 프로젝트 수 등을 그대로 복사합니다.
   *
   * @param source 변환할 원본 UserDataResponse
   * @return 매핑된 GetOtherUserDataResponse
   */
  public GetOtherUserDataResponse toOtherUserData(UserDataResponse source) {
    return new GetOtherUserDataResponse(
        source.id(),
        source.title(),
        source.topicLabel(),
        source.dataTypeLabel(),
        source.startDate(),
        source.endDate(),
        source.dataThumbnailUrl(),
        source.downloadCount(),
        source.sizeBytes(),
        source.rowCount(),
        source.columnCount(),
        source.createdAt(),
        source.countConnectedProjects());
  }
}
