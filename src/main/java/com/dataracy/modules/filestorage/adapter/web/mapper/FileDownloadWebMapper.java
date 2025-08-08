package com.dataracy.modules.filestorage.adapter.web.mapper;

import com.dataracy.modules.filestorage.adapter.web.response.GetPresignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPresignedUrlResponse;

public class FileDownloadWebMapper {
    public static GetPresignedUrlWebResponse toWebDto(GetPresignedUrlResponse responseDto) {
        return new GetPresignedUrlWebResponse(
                responseDto.preSignedUrl()
        );
    }
}
