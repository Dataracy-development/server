package com.dataracy.modules.dataset.adapter.web.mapper.command;

import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import org.springframework.stereotype.Component;

@Component
public class DataCommandWebMapper {
    /**
     * UploadDataWebRequest 객체를 UploadDataRequest DTO로 변환합니다.
     *
     * 웹 계층의 데이터 업로드 요청을 애플리케이션 계층에서 사용하는 DTO로 매핑합니다.
     *
     * @param webRequest 변환할 데이터 업로드 웹 요청 객체
     * @return 매핑된 UploadDataRequest DTO
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
     * 웹 요청 객체를 애플리케이션 계층의 데이터 수정 요청 DTO로 변환합니다.
     *
     * @param webRequest 데이터 수정 정보를 포함한 웹 요청 객체
     * @return 변환된 데이터 수정 요청 DTO
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
