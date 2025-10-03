/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.adapter.web.mapper.read;

import org.springframework.stereotype.Component;

import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;

/** 타 어그리거트로부터 받아 변환한 유저 정보를 토대로 한 애플리케이션 DTO와 웹 DTO를 변환하는 매퍼 */
@Component
public class OtherUserReadWebMapper {
  /**
   * 다른 사용자 정보 응답 애플리케이션 DTO를 웹 응답 DTO로 변환한다.
   *
   * <p>입력된 {@code GetOtherUserInfoResponse}에서 id, nickname, authorLevelLabel, occupationLabel,
   * profileImageUrl, introductionText 필드를 복사하고, 포함된 프로젝트 목록과 데이터셋 목록은 이 매퍼의 대응되는 {@code toWebDto}
   * 오버로드를 통해 각각 변환하여 {@code GetOtherUserInfoWebResponse}를 생성한다.
   *
   * @param responseDto 변환할 애플리케이션 레벨의 사용자 정보 응답
   * @return 변환된 웹 레벨의 사용자 정보 응답
   */
  public GetOtherUserInfoWebResponse toWebDto(GetOtherUserInfoResponse responseDto) {
    return new GetOtherUserInfoWebResponse(
        responseDto.id(),
        responseDto.nickname(),
        responseDto.authorLevelLabel(),
        responseDto.occupationLabel(),
        responseDto.profileImageUrl(),
        responseDto.introductionText(),
        responseDto.projects().map(this::toWebDto),
        responseDto.datasets().map(this::toWebDto));
  }

  /**
   * 애플리케이션 레이어의 GetOtherUserProjectResponse를 웹 레이어 DTO인 GetOtherUserProjectWebResponse로 변환한다.
   *
   * <p>변환된 웹 DTO에는 프로젝트 식별자(id), 제목, 내용, 썸네일 URL, 주제 레이블(topicLabel), 작성자 레벨 레이블(authorLevelLabel),
   * 댓글/좋아요/조회수, 생성일(createdAt) 등이 포함된다.
   *
   * @param responseDto 변환할 애플리케이션 레이어의 프로젝트 응답 객체
   * @return 웹 레이어에서 사용될 GetOtherUserProjectWebResponse 인스턴스
   */
  public GetOtherUserProjectWebResponse toWebDto(GetOtherUserProjectResponse responseDto) {
    return new GetOtherUserProjectWebResponse(
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

  /**
   * 애플리케이션 계층의 GetOtherUserDataResponse를 웹 계층의 GetOtherUserDataWebResponse로 변환한다.
   *
   * <p>요청된 데이터의 식별자, 제목, 토픽/데이터 타입 라벨, 기간(start/end), 썸네일 URL, 다운로드/크기/행·열 수, 생성일(createdAt) 및 연관
   * 프로젝트 수를 대응하는 웹 DTO 필드로 그대로 복사하여 새 웹 DTO를 생성해 반환한다.
   *
   * @param responseDto 변환할 애플리케이션 레이어의 응답 객체
   * @return 변환된 GetOtherUserDataWebResponse 인스턴스
   */
  public GetOtherUserDataWebResponse toWebDto(GetOtherUserDataResponse responseDto) {
    return new GetOtherUserDataWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.topicLabel(),
        responseDto.dataTypeLabel(),
        responseDto.startDate(),
        responseDto.endDate(),
        responseDto.dataThumbnailUrl(),
        responseDto.downloadCount(),
        responseDto.sizeBytes(),
        responseDto.rowCount(),
        responseDto.columnCount(),
        responseDto.createdAt(),
        responseDto.countConnectedProjects());
  }
}
