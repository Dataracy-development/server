package com.dataracy.modules.dataset.adapter.web.mapper;

import com.dataracy.modules.dataset.adapter.web.request.DataModifyWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.ConnectedDataAssociatedWithProjectWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.CountDataGroupWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.DataDetailWebResponse;
import com.dataracy.modules.dataset.application.dto.request.DataModifyRequest;
import com.dataracy.modules.dataset.application.dto.request.DataUploadRequest;
import com.dataracy.modules.dataset.application.dto.response.ConnectedDataAssociatedWithProjectResponse;
import com.dataracy.modules.dataset.application.dto.response.CountDataGroupResponse;
import com.dataracy.modules.dataset.application.dto.response.DataDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class DataWebMapper {
    /**
     * DataUploadWebRequest 객체를 애플리케이션 계층의 DataUploadRequest DTO로 변환합니다.
     *
     * 웹 계층에서 전달된 데이터 업로드 요청 정보를 DataUploadRequest 객체로 매핑하여 반환합니다.
     *
     * @param webRequest 변환할 데이터 업로드 웹 요청 객체
     * @return 매핑된 DataUploadRequest 객체
     */
    public DataUploadRequest toApplicationDto(DataUploadWebRequest webRequest) {
        return new DataUploadRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.dataSourceId(),
                webRequest.dataTypeId(),
                webRequest.startDate(),
                webRequest.endDate(),
                webRequest.description(),
                webRequest.analysisGuide()
        );
    }

    public DataModifyRequest toApplicationDto(DataModifyWebRequest webRequest) {
        return new DataModifyRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.dataSourceId(),
                webRequest.dataTypeId(),
                webRequest.startDate(),
                webRequest.endDate(),
                webRequest.description(),
                webRequest.analysisGuide()
        );
    }

    /**
     * 애플리케이션 계층의 데이터 상세 응답 DTO를 웹 계층의 응답 객체로 변환합니다.
     *
     * @param responseDto 데이터 상세 정보를 담고 있는 애플리케이션 계층 DTO
     * @return 웹 계층에서 사용할 데이터 상세 응답 객체
     */
    public DataDetailWebResponse toWebDto(DataDetailResponse responseDto) {
        return new DataDetailWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.authorLabel(),
                responseDto.occupationLabel(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.analysisGuide(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.recentWeekDownloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.previewJson(),
                responseDto.createdAt()
        );
    }

    /**
     * CountDataGroupResponse 객체를 CountDataGroupWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 애플리케이션 계층의 CountDataGroupResponse 객체
     * @return 웹 계층에서 사용하는 CountDataGroupWebResponse 객체
     */
    public CountDataGroupWebResponse toWebDto(CountDataGroupResponse responseDto) {
        return new CountDataGroupWebResponse(
                responseDto.topicId(),
                responseDto.topicLabel(),
                responseDto.count()
        );
    }

    /**
     * 애플리케이션 계층의 ConnectedDataAssociatedWithProjectResponse DTO를 웹 계층의 ConnectedDataAssociatedWithProjectWebResponse로 변환합니다.
     *
     * @param responseDto 변환할 데이터셋 연결 정보 DTO
     * @return 변환된 웹 응답 객체
     */
    public ConnectedDataAssociatedWithProjectWebResponse toWebDto(ConnectedDataAssociatedWithProjectResponse responseDto) {
        return new ConnectedDataAssociatedWithProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
