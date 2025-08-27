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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileDownloadControllerTest {

    @Mock
    private DownloadFileUseCase downloadFileUseCase;

    @Mock
    private FileDownloadWebMapper fileDownloadWebMapper;

    @InjectMocks
    private FileDownloadController controller;

    @Test
    @DisplayName("파일 다운로드 URL 반환")
    void getPreSignedUrlShouldMapAndWrapSuccessResponse() {
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
