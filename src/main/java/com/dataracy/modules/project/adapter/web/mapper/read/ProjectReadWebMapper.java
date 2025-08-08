package com.dataracy.modules.project.adapter.web.mapper.read;

import com.dataracy.modules.project.adapter.web.response.read.ConnectedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ContinuedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.PopularProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ProjectDetailWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectReadWebMapper {
    /**
     * 애플리케이션 계층의 프로젝트 상세 응답 DTO를 웹 계층의 프로젝트 상세 응답 DTO로 변환합니다.
     *
     * @param responseDto 변환할 프로젝트 상세 정보 응답 DTO
     * @return 변환된 웹 계층의 프로젝트 상세 정보 응답 DTO
     */
    public ProjectDetailWebResponse toWebDto(ProjectDetailResponse responseDto) {
        return new ProjectDetailWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
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
                responseDto.hasDataSet()
        );
    }

    /**
     * 애플리케이션 계층의 ContinuedProjectResponse를 웹 계층의 ContinuedProjectWebResponse로 변환합니다.
     *
     * @param responseDto 변환할 ContinuedProjectResponse 객체
     * @return 변환된 ContinuedProjectWebResponse 객체
     */
    public ContinuedProjectWebResponse toWebDto(ContinuedProjectResponse responseDto) {
        return new ContinuedProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
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
     * @param responseDto 변환할 프로젝트 연결 응답 DTO
     * @return 변환된 웹 계층 프로젝트 연결 응답 DTO
     */
    public ConnectedProjectWebResponse toWebDto(ConnectedProjectResponse responseDto) {
        return new ConnectedProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.topicLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }

    /**
     * ChildProjectResponse 객체를 ChildProjectWebResponse 객체로 변환합니다.
     *
     * 프로젝트 자식의 ID, 제목, 내용, 작성자, 댓글 수, 좋아요 수를 포함하는 웹 레이어 응답 DTO를 생성합니다.
     *
     * @param responseDto 변환할 프로젝트 자식 응답 DTO
     * @return 변환된 ChildProjectWebResponse 객체
     */
    public ChildProjectWebResponse toWebDto(ChildProjectResponse responseDto) {
        return new ChildProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
                responseDto.commentCount(),
                responseDto.likeCount()
        );
    }

    /**
     * 인기 프로젝트 응답 DTO를 웹 계층의 응답 객체로 변환합니다.
     *
     * @param responseDto 애플리케이션 계층의 인기 프로젝트 정보를 담은 DTO
     * @return 웹 계층에서 사용하는 인기 프로젝트 응답 객체
     */
    public PopularProjectWebResponse toWebDto(PopularProjectResponse responseDto) {
        return new PopularProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
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
