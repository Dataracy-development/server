package com.dataracy.modules.dataset.adapter.web.mapper.download;

import com.dataracy.modules.dataset.adapter.web.response.download.GetDataPreSignedUrlWebResponse;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import org.springframework.stereotype.Component;

@Component
public class DataDownloadWebMapper {
    public GetDataPreSignedUrlWebResponse toWebDto(GetDataPreSignedUrlResponse responseDto) {
        return new GetDataPreSignedUrlWebResponse(
                responseDto.preSignedUrl()
        );
    }
}
