package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.AuthorLevelWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import org.springframework.stereotype.Component;

/**
 * authorLevel 웹 DTO와 authorLevel 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AuthorLevelWebMapper {
    // authorLevel 조회 도메인 응답 DTO -> authorLevel 조회 웹 응답 DTO
    public AuthorLevelWebResponse toWebDto(AuthorLevelResponse authorLevelResponse) {
        return new AuthorLevelWebResponse(
                authorLevelResponse.id(),
                authorLevelResponse.value(),
                authorLevelResponse.label()
        );
    }

    // 전체 authorLevel 리스트 조회 도메인 응답 DTO -> 전체 authorLevel 리스트 조회 웹 응답 DTO
    public AllAuthorLevelsWebResponse toWebDto(AllAuthorLevelsResponse allAuthorLevelsResponse) {
        return new AllAuthorLevelsWebResponse(
                allAuthorLevelsResponse.authorLevels()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
