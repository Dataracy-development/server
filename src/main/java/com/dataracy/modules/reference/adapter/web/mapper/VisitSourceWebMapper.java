package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import org.springframework.stereotype.Component;

/**
 * visitSource 웹 DTO와 visitSource 도메인 DTO를 변환하는 매퍼
 */
@Component
public class VisitSourceWebMapper {
    /**
     * 도메인 계층의 방문 소스 응답 DTO를 웹 계층의 방문 소스 응답 DTO로 변환합니다.
     *
     * @param visitSourceResponse 변환할 도메인 방문 소스 응답 DTO
     * @return 변환된 웹 방문 소스 응답 DTO
     */
    public VisitSourceWebResponse toWebDto(VisitSourceResponse visitSourceResponse) {
        return new VisitSourceWebResponse(
                visitSourceResponse.id(),
                visitSourceResponse.value(),
                visitSourceResponse.label()
        );
    }

    // 전체 visitSource 리스트 조회 도메인 응답 DTO -> 전체 visitSource 리스트 조회 웹 응답 DTO
    public AllVisitSourcesWebResponse toWebDto(AllVisitSourcesResponse allVisitSourcesResponse) {
        return new AllVisitSourcesWebResponse(
                allVisitSourcesResponse.visitSources()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
