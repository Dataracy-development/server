package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectDetailDtoMapper {
    /**
     * 프로젝트 도메인 객체와 관련 추가 정보를 결합하여 ProjectDetailResponse DTO로 변환합니다.
     *
     * @param project 변환할 프로젝트 도메인 객체
     * @param username 작성자 이름
     * @param userIntroductionText 작성자 소개 텍스트
     * @param authorLevelLabel 작성자 등급 라벨
     * @param occupationLabel 직업 라벨
     * @param topicLabel 주제 라벨
     * @param analysisPurposeLabel 분석 목적 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param isLiked 사용자가 해당 프로젝트에 좋아요를 눌렀는지 여부
     * @param hasChild 하위 프로젝트 존재 여부
     * @param connectedDataSets 프로젝트와 연결된 데이터셋 목록
     * @return 프로젝트 상세 정보를 담은 ProjectDetailResponse DTO
     */
    public ProjectDetailResponse toResponseDto(
            Project project,
            String username,
            String userIntroductionText,
            String authorLevelLabel,
            String occupationLabel,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            boolean isLiked,
            boolean hasChild,
            List<ProjectConnectedDataResponse> connectedDataSets
    ) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                username,
                userIntroductionText,
                authorLevelLabel,
                occupationLabel,
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                project.getIsContinue(),
                project.getParentProjectId(),
                project.getContent(),
                project.getThumbnailUrl(),
                project.getCreatedAt(),
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                isLiked,
                hasChild,
                connectedDataSets
        );
    }
}
