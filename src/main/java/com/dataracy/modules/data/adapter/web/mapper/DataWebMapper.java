package com.dataracy.modules.data.adapter.web.mapper;

import com.dataracy.modules.data.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import org.springframework.stereotype.Component;

@Component
public class DataWebMapper {
    /**
     * DataUploadWebRequest 객체를 DataUploadRequest DTO로 매핑합니다.
     *
     * 웹 요청에서 전달된 데이터 업로드 정보를 애플리케이션 계층에서 사용하는 DataUploadRequest 객체로 변환합니다.
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
}
