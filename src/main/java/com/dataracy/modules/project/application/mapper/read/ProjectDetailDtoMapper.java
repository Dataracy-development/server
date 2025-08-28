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
     * Project 도메인과 전달된 메타데이터를 합쳐 ProjectDetailResponse DTO를 생성합니다.
     *
     * Project의 필수 도메인 값(id, title, userId, isContinue, parentProjectId, content, thumbnailUrl,
     * createdAt, commentCount, likeCount, viewCount 등)은 전달된 `project`의 게터에서 읽어오며,
     * 작성자 정보(username, userIntroductionText, userProfileImageUrl), 라벨(authorLevelLabel,
     * occupationLabel, topicLabel, analysisPurposeLabel, dataSourceLabel), 사용자별 상태(isLiked),
     * 자식 존재 여부(hasChild), 연결된 데이터(connectedDataSets), 부모 프로젝트 정보(parentProjectResponse)
     * 는 각 파라미터로 채웁니다.
     *
     * @param project 도메인 소스가 되는 Project 객체(도메인 필드들을 이 객체의 게터에서 읽어옵니다)
     * @param isLiked 호출자(사용자)가 해당 프로젝트를 좋아요했는지 여부
     * @param hasChild 해당 프로젝트에 하위(자식) 프로젝트가 존재하는지 여부
     * @param connectedDataSets 프로젝트와 연결된 데이터셋 응답 객체 목록
     * @param parentProjectResponse 부모 프로젝트 정보(없을 경우 null 가능)
     * @return 합쳐진 정보를 담은 ProjectDetailResponse 인스턴스
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
                project.getUserId(),
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
