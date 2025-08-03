package com.dataracy.modules.project.adapter.web.mapper.search;

import com.dataracy.modules.project.adapter.web.response.search.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.search.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectSearchWebMapper {
    /**
     * ProjectRealTimeSearchResponse DTO를 ProjectRealTimeSearchWebResponse 웹 응답 객체로 변환합니다.
     *
     * @param responseDto 변환할 실시간 프로젝트 검색 응답 DTO
     * @return 변환된 웹 응답 객체
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
     * ProjectSimilarSearchResponse DTO를 ProjectSimilarSearchWebResponse 웹 응답 객체로 변환합니다.
     *
     * 유사 프로젝트 검색 결과 DTO의 모든 필드를 웹 응답 객체에 매핑하여 반환합니다.
     *
     * @param responseDto 변환할 ProjectSimilarSearchResponse DTO
     * @return 변환된 ProjectSimilarSearchWebResponse 객체
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

    /**
     * ProjectPopularSearchResponse DTO를 ProjectPopularSearchWebResponse 웹 응답 객체로 변환합니다.
     *
     * @param responseDto 변환할 인기 프로젝트 검색 응답 DTO
     * @return 변환된 인기 프로젝트 검색 웹 응답 객체
     */
    public PopularProjectWebResponse toWeb(PopularProjectResponse responseDto) {
        return new PopularProjectWebResponse(
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
