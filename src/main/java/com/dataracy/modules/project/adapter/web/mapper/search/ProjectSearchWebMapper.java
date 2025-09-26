package com.dataracy.modules.project.adapter.web.mapper.search;

import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectSearchWebMapper {
    /**
     * RealTimeProjectResponse DTO를 RealTimeProjectWebResponse로 변환합니다.
     *
     * DTO의 id, title, creatorId, creatorName, projectThumbnailUrl 필드를 대응하는 웹 응답 객체로 매핑합니다.
     *
     * @param responseDto 변환할 실시간 프로젝트 검색 결과 DTO
     * @return 변환된 RealTimeProjectWebResponse 객체
     */
    public RealTimeProjectWebResponse toWebDto(RealTimeProjectResponse responseDto) {
        return new RealTimeProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.projectThumbnailUrl()
        );
    }

    /**
     * SimilarProjectResponse를 SimilarProjectWebResponse로 변환합니다.
     *
     * DTO의 필드들을 대응되는 웹 응답 필드로 매핑하여 유사 프로젝트 검색 결과의 응답 객체를 생성합니다.
     *
     * @param responseDto 변환할 SimilarProjectResponse DTO
     * @return 변환된 SimilarProjectWebResponse 객체
     */
    public SimilarProjectWebResponse toWebDto(SimilarProjectResponse responseDto) {
        return new SimilarProjectWebResponse(
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
                responseDto.viewCount()
        );
    }
}
