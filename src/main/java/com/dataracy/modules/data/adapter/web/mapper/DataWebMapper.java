package com.dataracy.modules.data.adapter.web.mapper;

import com.dataracy.modules.data.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import org.springframework.stereotype.Component;

@Component
public class DataWebMapper {
    public DataUploadRequest toApplicationDto(DataUploadWebRequest webRequest) {
        return new DataUploadRequest(
                webRequest.title(),
                webRequest.topicId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId(),
                webRequest.startDate(),
                webRequest.endDate(),
                webRequest.description(),
                webRequest.analysisGuide()
        );
    }
}
