/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.web.mapper.read;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.project.adapter.web.mapper.support.ParentProjectWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.support.ProjectConnectedDataWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.*;
import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.read.*;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectReadWebMapper {
  private final ProjectConnectedDataWebMapper projectConnectedDataWebMapper;
  private final ParentProjectWebMapper parentProjectWebMapper;

  /**
   * 애플리케이션 계층의 프로젝트 상세 응답 DTO를 웹 계층의 프로젝트 상세 응답 DTO로 변환합니다.
   *
   * <p>프로젝트의 상세 정보와 연결된 데이터셋 목록을 포함하여 웹 계층에서 사용할 수 있는 형태로 매핑합니다.
   *
   * @param responseDto 변환할 프로젝트 상세 정보 응답 DTO
   * @return 웹 계층의 프로젝트 상세 정보 응답 DTO
   */
  public ProjectDetailWebResponse toWebDto(ProjectDetailResponse responseDto) {
    List<ProjectConnectedDataWebResponse> connectWebDataSets =
        responseDto.connectedDataSets().stream()
            .map(projectConnectedDataWebMapper::toWebDto)
            .toList();

    ParentProjectWebResponse parentProject =
        parentProjectWebMapper.toWebDto(responseDto.parentProject());

    return new ProjectDetailWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.userIntroductionText(),
        responseDto.authorLevelLabel(),
        responseDto.occupationLabel(),
        responseDto.topicLabel(),
        responseDto.analysisPurposeLabel(),
        responseDto.dataSourceLabel(),
        responseDto.isContinue(),
        responseDto.parentProjectId(),
        responseDto.content(),
        responseDto.projectThumbnailUrl(),
        responseDto.createdAt(),
        responseDto.commentCount(),
        responseDto.likeCount(),
        responseDto.viewCount(),
        responseDto.isLiked(),
        responseDto.hasChild(),
        connectWebDataSets,
        parentProject);
  }

  /**
   * 애플리케이션 계층의 ContinuedProjectResponse를 웹 계층의 ContinuedProjectWebResponse로 변환합니다.
   *
   * <p>요청된 프로젝트의 식별자, 제목, 작성자 정보(아이디·이름·프로필 이미지), 썸네일, 라벨과 통계(댓글·좋아요·조회수) 및 생성시각을 web DTO로 복사하여
   * 생성합니다.
   *
   * @param responseDto 변환할 ContinuedProjectResponse 객체
   * @return 변환된 ContinuedProjectWebResponse 객체
   */
  public ContinuedProjectWebResponse toWebDto(ContinuedProjectResponse responseDto) {
    return new ContinuedProjectWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.projectThumbnailUrl(),
        responseDto.topicLabel(),
        responseDto.authorLevelLabel(),
        responseDto.commentCount(),
        responseDto.likeCount(),
        responseDto.viewCount(),
        responseDto.createdAt());
  }

  /**
   * 애플리케이션 계층의 ConnectedProjectResponse를 웹 계층의 ConnectedProjectWebResponse로 변환합니다.
   *
   * @param responseDto 변환할 프로젝트 연결 응답 DTO
   * @return 변환된 웹 계층 프로젝트 연결 응답 DTO
   */
  public ConnectedProjectWebResponse toWebDto(ConnectedProjectResponse responseDto) {
    return new ConnectedProjectWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.topicLabel(),
        responseDto.commentCount(),
        responseDto.likeCount(),
        responseDto.viewCount(),
        responseDto.createdAt());
  }

  /**
   * ChildProjectResponse 객체를 ChildProjectWebResponse 객체로 변환합니다.
   *
   * <p>응답 DTO의 id, 제목, 내용, 작성자 정보(작성자 id·이름·프로필 이미지 URL)와 댓글/좋아요 수를 사용해 웹 레이어용
   * ChildProjectWebResponse를 생성합니다.
   *
   * @param responseDto 변환할 프로젝트 자식 응답 DTO
   * @return 변환된 ChildProjectWebResponse 객체
   */
  public ChildProjectWebResponse toWebDto(ChildProjectResponse responseDto) {
    return new ChildProjectWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.content(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.commentCount(),
        responseDto.likeCount());
  }

  /**
   * 애플리케이션 계층의 PopularProjectResponse를 웹 계층의 PopularProjectWebResponse로 변환합니다.
   *
   * <p>변환 시 프로젝트의 식별자, 제목, 내용, 작성자(아이디·이름), 썸네일, 레이블류 및 통계(count) 필드를 복사합니다.
   *
   * @param responseDto 변환할 인기 프로젝트의 애플리케이션 계층 DTO
   * @return 변환된 인기 프로젝트 웹 응답 객체
   */
  public PopularProjectWebResponse toWebDto(PopularProjectResponse responseDto) {
    return new PopularProjectWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.content(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.projectThumbnailUrl(),
        responseDto.topicLabel(),
        responseDto.analysisPurposeLabel(),
        responseDto.dataSourceLabel(),
        responseDto.authorLevelLabel(),
        responseDto.commentCount(),
        responseDto.likeCount(),
        responseDto.viewCount());
  }

  /**
   * 애플리케이션 계층의 UserProjectResponse를 웹 계층의 UserProjectWebResponse로 변환한다.
   *
   * @param responseDto 변환할 프로젝트 읽기 응답 객체(프로젝트 ID, 제목, 내용, 썸네일 URL, 라벨들, 카운트, 생성일 등 포함)
   * @return 웹 계층에서 사용하는 UserProjectWebResponse 인스턴스
   */
  public UserProjectWebResponse toWebDto(UserProjectResponse responseDto) {
    return new UserProjectWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.content(),
        responseDto.projectThumbnailUrl(),
        responseDto.topicLabel(),
        responseDto.authorLevelLabel(),
        responseDto.commentCount(),
        responseDto.likeCount(),
        responseDto.viewCount(),
        responseDto.createdAt());
  }
}
