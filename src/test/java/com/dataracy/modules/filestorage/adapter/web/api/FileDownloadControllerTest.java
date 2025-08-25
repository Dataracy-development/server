package com.dataracy.modules.filestorage.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import com.dataracy.modules.filestorage.adapter.web.mapper.FileDownloadWebMapper;
import com.dataracy.modules.filestorage.adapter.web.response.GetPreSignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloadControllerTest {

    @Mock DownloadFileUseCase downloadFileUseCase;
    @Mock FileDownloadWebMapper fileDownloadWebMapper;

    @InjectMocks FileDownloadController controller;

    @Test
    @DisplayName("getPreSignedUrl_should_map_and_wrap_success_response")
    void getPreSignedUrl_should_map_and_wrap_success_response() {
        // given
        String s3Url = "https://bucket/k";
        int expiration = 300;
        GetPreSignedUrlResponse dto = new GetPreSignedUrlResponse("https://signed");
        GetPreSignedUrlWebResponse web = new GetPreSignedUrlWebResponse("https://signed");
        given(downloadFileUseCase.generatePreSignedUrl(s3Url, expiration)).willReturn(dto);
        given(fileDownloadWebMapper.toWebDto(dto)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<GetPreSignedUrlWebResponse>> resp =
            controller.getPreSignedUrl(s3Url, expiration);

        // then
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo(CommonSuccessStatus.OK.getMessage());
        assertThat(resp.getBody().getData().preSignedUrl()).isEqualTo("https://signed");
        then(downloadFileUseCase).should().generatePreSignedUrl(s3Url, expiration);
        then(fileDownloadWebMapper).should().toWebDto(dto);
    }
}
