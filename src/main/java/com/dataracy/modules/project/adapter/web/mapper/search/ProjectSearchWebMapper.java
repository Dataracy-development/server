package com.dataracy.modules.project.adapter.web.mapper.search;

import com.dataracy.modules.project.adapter.web.response.read.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectSearchWebMapper {
    /**
     * 실시간 프로젝트 검색 응답 DTO를 웹 응답 객체로 변환합니다.
     *
     * @param responseDto 실시간 프로젝트 검색 결과를 담고 있는 DTO
     * @return 변환된 실시간 프로젝트 웹 응답 객체
     */
    public RealTimeProjectWebResponse toWeb(RealTimeProjectResponse responseDto) {
        return new RealTimeProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.fileUrl()
        );
    }

    /**
     * SimilarProjectResponse DTO를 SimilarProjectWebResponse 웹 응답 객체로 변환합니다.
     *
     * 유사 프로젝트 검색 결과 DTO의 모든 필드를 동일하게 웹 응답 객체에 매핑하여 반환합니다.
     *
     * @param responseDto 변환할 SimilarProjectResponse DTO
     * @return 변환된 SimilarProjectWebResponse 객체
     */
    public SimilarProjectWebResponse toWeb(SimilarProjectResponse responseDto) {
        return new SimilarProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
                responseDto.fileUrl(),
                responseDto.topicLabel(),
                responseDto.analysisPurposeLabel(),
                responseDto.dataSourceLabel(),
                responseDto.authorLevelLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount()
        );
    }
}
