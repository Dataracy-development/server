package com.dataracy.modules.dataset.adapter.web.mapper.command;

import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import org.springframework.stereotype.Component;

@Component
public class DataCommandWebMapper {
    /**
     * DataUploadWebRequest 객체를 DataUploadRequest DTO로 변환합니다.
     *
     * 웹 계층에서 전달된 데이터 업로드 요청 정보를 애플리케이션 계층의 DTO로 매핑합니다.
     *
     * @param webRequest 데이터 업로드 웹 요청 객체
     * @return 변환된 DataUploadRequest DTO
     */
    public UploadDataRequest toApplicationDto(UploadDataWebRequest webRequest) {
        return new UploadDataRequest(
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
     * 웹 계층의 DataModifyWebRequest 객체를 애플리케이션 계층의 DataModifyRequest DTO로 변환합니다.
     *
     * @param webRequest 데이터 수정 요청 정보를 담은 웹 요청 객체
     * @return 변환된 DataModifyRequest DTO
     */
    public ModifyDataRequest toApplicationDto(ModifyDataWebRequest webRequest) {
        return new ModifyDataRequest(
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
