package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.request.ProjectModifyWebRequest;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.adapter.web.response.ConnectedProjectAssociatedWithDataWebResponse;
import com.dataracy.modules.project.adapter.web.response.ContinueProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.ProjectDetailWebResponse;
import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.dto.response.ConnectedProjectAssociatedWithDataResponse;
import com.dataracy.modules.project.application.dto.response.ContinueProjectResponse;
import com.dataracy.modules.project.application.dto.response.ProjectDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectWebMapper {
    /**
     * 웹 계층의 프로젝트 업로드 요청 DTO를 애플리케이션 계층의 프로젝트 업로드 요청 DTO로 변환합니다.
     *
     * @param webRequest 변환할 프로젝트 업로드 웹 요청 DTO
     * @return 애플리케이션 계층에서 사용할 프로젝트 업로드 요청 DTO
     */
    public ProjectUploadRequest toApplicationDto(ProjectUploadWebRequest webRequest) {
        return new ProjectUploadRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId(),
                webRequest.isContinue(),
                webRequest.parentProjectId(),
                webRequest.content(),
                webRequest.dataIds()
        );
    }

    /**
     * 웹 계층의 프로젝트 수정 요청 DTO를 애플리케이션 계층의 프로젝트 수정 요청 DTO로 변환합니다.
     *
     * @param webRequest 프로젝트 수정 정보를 담은 웹 요청 DTO
     * @return 변환된 애플리케이션 계층의 프로젝트 수정 요청 DTO
     */
    public ProjectModifyRequest toApplicationDto(ProjectModifyWebRequest webRequest) {
        return new ProjectModifyRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId(),
                webRequest.isContinue(),
                webRequest.parentProjectId(),
                webRequest.content(),
                webRequest.dataIds()
        );
    }

    /**
     * 애플리케이션 계층의 프로젝트 상세 응답 DTO를 웹 계층의 프로젝트 상세 응답 DTO로 변환합니다.
     *
     * @param responseDto 애플리케이션 계층의 프로젝트 상세 정보 DTO
     * @return 웹 계층의 프로젝트 상세 정보 DTO
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
                responseDto.fileUrl(),
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
     * 애플리케이션 계층의 프로젝트 이어하기 응답 DTO를 웹 계층의 응답 DTO로 변환합니다.
     *
     * @param responseDto 변환할 프로젝트 이어하기 응답 DTO
     * @return 변환된 웹 계층의 프로젝트 이어하기 응답 DTO
     */
    public ContinueProjectWebResponse toWebDto(ContinueProjectResponse responseDto) {
        return new ContinueProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.userThumbnailUrl(),
                responseDto.fileUrl(),
                responseDto.topicLabel(),
                responseDto.authorLevelLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }

    /**
     * 애플리케이션 계층의 ConnectedProjectAssociatedWithDataResponse를 웹 계층의 ConnectedProjectAssociatedWithDataWebResponse로 변환합니다.
     *
     * @param responseDto 변환할 프로젝트 연결 데이터 응답 DTO
     * @return 변환된 웹 계층 프로젝트 연결 데이터 응답 DTO
     */
    public ConnectedProjectAssociatedWithDataWebResponse toWebDto(ConnectedProjectAssociatedWithDataResponse responseDto) {
        return new ConnectedProjectAssociatedWithDataWebResponse(
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
}
