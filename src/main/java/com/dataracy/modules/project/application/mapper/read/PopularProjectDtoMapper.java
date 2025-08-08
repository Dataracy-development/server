package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

/**
 * 인기있는 프로젝트 도메인 DTO와 인기있는 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class PopularProjectDtoMapper {
    /**
     * 프로젝트 도메인 객체와 관련 라벨, 작성자명을 받아 인기 프로젝트 응답 DTO로 변환합니다.
     *
     * @param project 변환할 프로젝트 도메인 객체
     * @param username 프로젝트 작성자명
     * @param topicLabel 프로젝트 주제 라벨
     * @param analysisPurposeLabel 분석 목적 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param authorLevelLabel 작성자 등급 라벨
     * @return 프로젝트 정보와 라벨, 작성자명이 포함된 인기 프로젝트 응답 DTO
     */
    public PopularProjectResponse toResponseDto(
            Project project,
            String username,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            String authorLevelLabel
    ) {
        return new PopularProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getContent(),
                username,
                project.getThumbnailUrl(),
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                authorLevelLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount()
        );
    }
}
