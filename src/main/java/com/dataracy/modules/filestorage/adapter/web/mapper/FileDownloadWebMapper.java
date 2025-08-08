package com.dataracy.modules.filestorage.adapter.web.mapper;

import com.dataracy.modules.filestorage.adapter.web.response.GetPresignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPresignedUrlResponse;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadWebMapper {
    public GetPresignedUrlWebResponse toWebDto(GetPresignedUrlResponse responseDto) {
        return new GetPresignedUrlWebResponse(
                responseDto.preSignedUrl()
        );
    }
}
