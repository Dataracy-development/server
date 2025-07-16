package com.dataracy.modules.data.adapter.web.mapper;

import com.dataracy.modules.data.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import org.springframework.stereotype.Component;

@Component
public class DataWebMapper {
    /**
     * DataUploadWebRequest 객체를 DataUploadRequest 애플리케이션 DTO로 변환합니다.
     *
     * @param webRequest 변환할 웹 요청 객체
     * @return 변환된 DataUploadRequest 객체
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
}
