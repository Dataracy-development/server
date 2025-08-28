package com.dataracy.modules.project.adapter.web.mapper.read;

import com.dataracy.modules.project.adapter.web.mapper.support.ParentProjectWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.support.ProjectConnectedDataWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.ConnectedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ContinuedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ProjectDetailWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectReadWebMapper {
    private final ProjectConnectedDataWebMapper projectConnectedDataWebMapper;
    private final ParentProjectWebMapper parentProjectWebMapper;

    /**
     * 애플리케이션 계층의 ProjectDetailResponse를 웹 계층의 ProjectDetailWebResponse로 변환합니다.
     *
     * 연결된 데이터셋 목록은 projectConnectedDataWebMapper로, 부모 프로젝트 정보는 parentProjectWebMapper로 각각 변환되어 결과 DTO에 포함됩니다.
     *
     * @param responseDto 변환할 애플리케이션 계층의 프로젝트 상세 응답 DTO
     * @return 웹 계층에서 사용하는 프로젝트 상세 응답 DTO
     */
    public ProjectDetailWebResponse toWebDto(ProjectDetailResponse responseDto) {
        List<ProjectConnectedDataWebResponse> connectWebDataSets = responseDto.connectedDataSets().stream()
                .map(projectConnectedDataWebMapper::toWebDto)
                .toList();

        ParentProjectWebResponse parentProject = parentProjectWebMapper.toWebDto(responseDto.parentProject());

        return new ProjectDetailWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userIntroductionText(),
                responseDto.userProfileImageUrl(),
                responseDto.authorLevelLabel(),
                responseDto.occupationLabel(),
                responseDto.topicLabel(),
                responseDto.analysisPurposeLabel(),
                responseDto.dataSourceLabel(),
                responseDto.isContinue(),
                responseDto.parentProjectId(),
                responseDto.content(),
                responseDto.projectThumbnailUrl(),
                responseDto.createdAt(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.isLiked(),
                responseDto.hasChild(),
                connectWebDataSets,
                parentProject
        );
    }

    /**
     * ContinuedProjectResponse를 ContinuedProjectWebResponse로 변환합니다.
     *
     * 애플리케이션 계층의 연속 프로젝트 읽기 DTO를 웹 계층 응답 DTO로 매핑하며,
     * creatorId와 creatorName을 웹 DTO의 대응 필드로 복사합니다.
     *
     * @param responseDto 변환할 ContinuedProjectResponse
     * @return 변환된 ContinuedProjectWebResponse
     */
    public ContinuedProjectWebResponse toWebDto(ContinuedProjectResponse responseDto) {
        return new ContinuedProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileUrl(),
                responseDto.projectThumbnailUrl(),
                responseDto.topicLabel(),
                responseDto.authorLevelLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }

    /**
     * 애플리케이션 계층의 ConnectedProjectResponse를 웹 계층의 ConnectedProjectWebResponse로 변환합니다.
     *
     * <p>응답 DTO의 주요 필드(id, title, creatorId, creatorName, topicLabel, commentCount,
     * likeCount, viewCount, createdAt)를 그대로 전달하여 웹용 DTO 인스턴스를 생성합니다.</p>
     *
     * @param responseDto 변환할 애플리케이션 계층의 ConnectedProjectResponse
     * @return 생성된 ConnectedProjectWebResponse
     */
    public ConnectedProjectWebResponse toWebDto(ConnectedProjectResponse responseDto) {
        return new ConnectedProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.topicLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }

    /**
     * ChildProjectResponse를 ChildProjectWebResponse로 변환합니다.
     *
     * 변환된 웹 응답에는 자식 프로젝트의 id, 제목, 내용, 작성자 ID와 이름(creatorId, creatorName),
     * 댓글 수, 좋아요 수가 포함됩니다.
     *
     * @param responseDto 변환할 ChildProjectResponse 객체
     * @return 변환된 ChildProjectWebResponse 객체
     */
    public ChildProjectWebResponse toWebDto(ChildProjectResponse responseDto) {
        return new ChildProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.commentCount(),
                responseDto.likeCount()
        );
    }

    /**
     * 애플리케이션 계층의 PopularProjectResponse를 웹 계층의 PopularProjectWebResponse로 변환합니다.
     *
     * 변환 시 프로젝트의 식별자, 제목, 내용, 작성자(아이디·이름), 썸네일, 레이블류 및 통계(count) 필드를 복사합니다.
     *
     * @param responseDto 변환할 인기 프로젝트의 애플리케이션 계층 DTO
     * @return 변환된 인기 프로젝트 웹 응답 객체
     */
    public PopularProjectWebResponse toWebDto(PopularProjectResponse responseDto) {
        return new PopularProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.creatorId(),
                responseDto.creatorName(),
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
