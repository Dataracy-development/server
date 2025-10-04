package com.dataracy.modules.reference.adapter.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.reference.adapter.web.response.allview.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.AuthorLevelWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;

/** authorLevel 웹 DTO와 authorLevel 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class AuthorLevelWebMapper {
  /**
   * 애플리케이션 계층의 AuthorLevelResponse 객체를 웹 계층의 AuthorLevelWebResponse 객체로 변환합니다.
   *
   * @param authorLevelResponse 변환할 애플리케이션 응답 DTO
   * @return 변환된 웹 응답 DTO
   */
  public AuthorLevelWebResponse toWebDto(AuthorLevelResponse authorLevelResponse) {
    return new AuthorLevelWebResponse(
        authorLevelResponse.id(), authorLevelResponse.value(), authorLevelResponse.label());
  }

  /**
   * 애플리케이션 계층의 전체 작성자 유형 리스트 응답 DTO를 웹 계층의 응답 DTO로 변환합니다.
   *
   * @param allAuthorLevelsResponse 애플리케이션 계층의 전체 작성자 유형 리스트 응답 DTO
   * @return 변환된 웹 계층의 전체 작성자 유형 리스트 응답 DTO
   */
  public AllAuthorLevelsWebResponse toWebDto(AllAuthorLevelsResponse allAuthorLevelsResponse) {
    if (allAuthorLevelsResponse == null || allAuthorLevelsResponse.authorLevels() == null) {
      return new AllAuthorLevelsWebResponse(List.of());
    }

    return new AllAuthorLevelsWebResponse(
        allAuthorLevelsResponse.authorLevels().stream().map(this::toWebDto).toList());
  }
}
