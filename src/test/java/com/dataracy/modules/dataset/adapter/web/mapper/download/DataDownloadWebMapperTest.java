package com.dataracy.modules.dataset.adapter.web.mapper.download;

import com.dataracy.modules.dataset.adapter.web.response.download.GetDataPreSignedUrlWebResponse;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataDownloadWebMapperTest {

    private final DataDownloadWebMapper mapper = new DataDownloadWebMapper();

    @Test
    @DisplayName("GetDataPreSignedUrlResponse → GetDataPreSignedUrlWebResponse 매핑 성공")
    void toWebDtoSuccess() {
        // given
        GetDataPreSignedUrlResponse responseDto = new GetDataPreSignedUrlResponse("https://signed-url.com/file.csv");

        // when
        GetDataPreSignedUrlWebResponse result = mapper.toWebDto(responseDto);

        // then
        assertThat(result.preSignedUrl()).isEqualTo("https://signed-url.com/file.csv");
    }
}
