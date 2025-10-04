package com.dataracy.modules.user.adapter.web.mapper.read;

import org.springframework.stereotype.Component;

import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;

import lombok.RequiredArgsConstructor;

/** 유저 조회 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
@RequiredArgsConstructor
public class UserReadWebMapper {
  /**
   * 애플리케이션 계층의 GetUserInfoResponse를 웹 계층의 GetUserInfoWebResponse로 변환한다.
   *
   * <p>필요한 식별자와 레이블(예: authorLevelId/Label, occupationId/Label, topicIds/topicLabels,
   * visitSourceId/Label) 및 기본 사용자 속성(id, role, email, nickname, profileImageUrl, introductionText)을
   * 대응하여 새 웹 DTO 인스턴스를 생성해 반환한다.
   *
   * @param responseDto 변환할 애플리케이션 계층의 사용자 정보 응답 DTO
   * @return 생성된 웹 계층의 사용자 정보 응답 DTO
   */
  public GetUserInfoWebResponse toWebDto(GetUserInfoResponse responseDto) {
    return new GetUserInfoWebResponse(
        responseDto.id(),
        responseDto.role(),
        responseDto.email(),
        responseDto.nickname(),
        responseDto.authorLevelId(),
        responseDto.authorLevelLabel(),
        responseDto.occupationId(),
        responseDto.occupationLabel(),
        responseDto.topicIds(),
        responseDto.topicLabels(),
        responseDto.visitSourceId(),
        responseDto.visitSourceLabel(),
        responseDto.profileImageUrl(),
        responseDto.introductionText());
  }
}
