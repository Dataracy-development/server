package com.dataracy.modules.filestorage.adapter.web.mapper;

import com.dataracy.modules.filestorage.adapter.web.response.GetPreSignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FileDownloadWebMapperTest {

    @Test
    @DisplayName("toWebDto_should_map_fields")
    void toWebDto_should_map_fields() {
        // given
        FileDownloadWebMapper mapper = new FileDownloadWebMapper();
        GetPreSignedUrlResponse dto = new GetPreSignedUrlResponse("http://signed");

        // when
        GetPreSignedUrlWebResponse web = mapper.toWebDto(dto);

        // then
        assertThat(web.preSignedUrl()).isEqualTo("http://signed");
    }
}
