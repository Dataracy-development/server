package com.dataracy.modules.dataset.adapter.web.mapper.download;

import com.dataracy.modules.dataset.adapter.web.response.download.GetDataPresignedUrlWebResponse;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPresignedUrlResponse;
import org.springframework.stereotype.Component;

@Component
public class DataDownloadWebMapper {
    public GetDataPresignedUrlWebResponse toWebDto(GetDataPresignedUrlResponse responseDto) {
        return new GetDataPresignedUrlWebResponse(
                responseDto.preSignedUrl()
        );
    }
}
