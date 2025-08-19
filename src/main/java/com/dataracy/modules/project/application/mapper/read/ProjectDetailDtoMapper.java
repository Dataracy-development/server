package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 프로젝트 상세정보 도메인 DTO와 프로젝트 상세정보 도메인 모델을 변환하는 매퍼
 */
@Component
public class ProjectDetailDtoMapper {
    /**
     * Project 도메인과 추가 메타데이터를 병합하여 ProjectDetailResponse DTO를 생성합니다.
     *
     * 주 도메인 정보는 전달된 Project의 게터에서 읽어오고, 작성자 정보·라벨·연결된 데이터 및 부모 프로젝트 정보는
     * 해당 파라미터로 채워집니다.
     *
     * @param isLiked 사용자가 해당 프로젝트에 대해 좋아요를 눌렀는지 여부
     * @param hasChild 하위(자식) 프로젝트가 존재하는지 여부
     * @param connectedDataSets 프로젝트와 연결된 데이터셋 응답 객체 목록
     * @param parentProjectResponse 부모 프로젝트 정보(없으면 null 가능)
     * @return 프로젝트 상세 정보를 담은 ProjectDetailResponse 인스턴스
     */
    public ProjectDetailResponse toResponseDto(
            Project project,
            String username,
            String userIntroductionText,
            String userProfileImageUrl,
            String authorLevelLabel,
            String occupationLabel,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            boolean isLiked,
            boolean hasChild,
            List<ProjectConnectedDataResponse> connectedDataSets,
            ParentProjectResponse parentProjectResponse
    ) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                username,
                userIntroductionText,
                userProfileImageUrl,
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
                connectedDataSets,
                parentProjectResponse
        );
    }
}
