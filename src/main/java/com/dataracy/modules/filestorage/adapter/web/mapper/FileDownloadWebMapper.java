package com.dataracy.modules.filestorage.adapter.web.mapper;

import com.dataracy.modules.filestorage.adapter.web.response.GetPreSignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadWebMapper {
    /**
     * GetPreSignedUrlResponse 객체를 GetPreSignedUrlWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 사전 서명 URL 응답 DTO
     * @return 변환된 웹 응답 DTO
     */
    public GetPreSignedUrlWebResponse toWebDto(GetPreSignedUrlResponse responseDto) {
        return new GetPreSignedUrlWebResponse(
                responseDto.preSignedUrl()
        );
    }
}
