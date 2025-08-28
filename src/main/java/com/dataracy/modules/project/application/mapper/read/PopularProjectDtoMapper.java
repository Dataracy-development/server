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
     * 프로젝트 도메인과 라벨/작성자 정보를 결합하여 PopularProjectResponse DTO를 생성합니다.
     *
     * <p>Project의 주요 필드(id, title, content, userId, thumbnailUrl, commentCount, likeCount, viewCount)
     * 와 전달된 라벨(topic, analysisPurpose, dataSource, authorLevel) 및 작성자명(username)을 사용해 DTO를 구성합니다.</p>
     *
     * @param project 변환할 프로젝트 도메인 객체(필수: id, title 등 주요 필드가 유효해야 함)
     * @param username 표시용 작성자명(화면에 표시할 이름)
     * @param topicLabel 프로젝트 주제 라벨
     * @param analysisPurposeLabel 분석 목적 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param authorLevelLabel 작성자 등급 라벨
     * @return 프로젝트 정보와 라벨, 작성자명이 포함된 PopularProjectResponse 인스턴스
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
                project.getUserId(),
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
