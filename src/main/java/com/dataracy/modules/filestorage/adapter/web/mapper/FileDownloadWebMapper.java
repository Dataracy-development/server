package com.dataracy.modules.filestorage.adapter.web.mapper;

import com.dataracy.modules.filestorage.adapter.web.response.GetPreSignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadWebMapper {
    public GetPreSignedUrlWebResponse toWebDto(GetPreSignedUrlResponse responseDto) {
        return new GetPreSignedUrlWebResponse(
                responseDto.preSignedUrl()
        );
    }
}
