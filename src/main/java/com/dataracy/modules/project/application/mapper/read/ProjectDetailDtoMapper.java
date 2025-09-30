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
     * Project 도메인과 추가 메타데이터를 병합해 ProjectDetailResponse를 생성합니다.
     *
     * Project 객체의 필드(아이디, 제목, 작성자 아이디, 계속 여부, 부모 프로젝트 ID, 내용, 썸네일, 생성일, 댓글/좋아요/조회수 등)
     * 를 응답 DTO의 기본 값으로 사용하고, 사용자명·프로필·라벨·연결 데이터·좋아요 여부 등은 전달된 파라미터로 채웁니다.
     * 
     * 참고: 13개의 파라미터를 가지지만, Mapper 레이어에서 도메인 객체와 여러 라벨 정보를 조합하여 상세 화면 DTO를 생성하므로 허용됩니다.
     *
     * @param isLiked 해당 사용자가 이 프로젝트에 좋아요를 눌렀는지 여부
     * @param hasChild 이 프로젝트가 하위(자식) 프로젝트를 가지고 있는지 여부
     * @param connectedDataSets 프로젝트와 연결된 데이터셋 응답 객체 목록
     * @param parentProjectResponse 부모 프로젝트 정보; 부모가 없으면 null 허용
     * @return 구성된 ProjectDetailResponse 인스턴스
     */
    @SuppressWarnings("java:S107") // Mapper 메서드로 여러 라벨 정보 조합 필요
    public ProjectDetailResponse toResponseDto(
            Project project,
            String username,
            String userProfileImageUrl,
            String userIntroductionText,
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
                userProfileImageUrl,
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
                connectedDataSets,
                parentProjectResponse
        );
    }
}
