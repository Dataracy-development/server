package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.response.ProjectRealTimeSearchWebResponse;
import com.dataracy.modules.project.adapter.web.response.ProjectSimilarSearchWebResponse;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectSearchWebMapper {
    /**
     * ProjectRealTimeSearchResponse 객체를 웹 응답 객체인 ProjectRealTimeSearchWebResponse로 변환합니다.
     *
     * @param responseDto 변환할 프로젝트 실시간 검색 응답 DTO
     * @return 변환된 웹 응답 객체
     */
    public ProjectRealTimeSearchWebResponse toWeb(ProjectRealTimeSearchResponse responseDto) {
        return new ProjectRealTimeSearchWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.fileUrl()
        );
    }

    public ProjectSimilarSearchWebResponse toWeb(ProjectSimilarSearchResponse responseDto) {
        return new ProjectSimilarSearchWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
                responseDto.fileUrl(),
                responseDto.topicLabel(),
                responseDto.analysisPurposeLabel(),
                responseDto.dataSourceLabel(),
                responseDto.authorLevelLabel()
        );
    }
}
