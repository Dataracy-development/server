package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloadServiceTest {

    @Mock
    FileStoragePort fileStoragePort;

    @InjectMocks
    FileDownloadService service;

    @Test
    @DisplayName("generatePreSignedUrl_success")
    void generatePreSignedUrl_success() {
        // given
        String s3Url = "https://bucket.s3.ap-northeast-2.amazonaws.com/path/to/file.jpg";
        int expirationSeconds = 300;
        given(fileStoragePort.getPreSignedUrl(s3Url, expirationSeconds)).willReturn("https://signed");

        // when
        GetPreSignedUrlResponse response = service.generatePreSignedUrl(s3Url, expirationSeconds);

        // then
        assertThat(response).isNotNull();
        assertThat(response.preSignedUrl()).isEqualTo("https://signed");
        then(fileStoragePort).should().getPreSignedUrl(s3Url, expirationSeconds);
    }

    @Test
    @DisplayName("generatePreSignedUrl_fail_when_url_blank")
    void generatePreSignedUrl_fail_when_url_blank() {
        // given
        String s3Url = "   ";
        int expirationSeconds = 100;

        // when
        IllegalArgumentException ex = catchThrowableOfType(
                () -> service.generatePreSignedUrl(s3Url, expirationSeconds),
                IllegalArgumentException.class);

        // then
        assertThat(ex).isNotNull();
        then(fileStoragePort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("generatePreSignedUrl_fail_when_expiration_leq_zero")
    void generatePreSignedUrl_fail_when_expiration_leq_zero() {
        // given
        String s3Url = "https://bucket.s3.ap-northeast-2.amazonaws.com/path/to/file.jpg";
        int expirationSeconds = 0;

        // when
        IllegalArgumentException ex = catchThrowableOfType(
                () -> service.generatePreSignedUrl(s3Url, expirationSeconds),
                IllegalArgumentException.class);

        // then
        assertThat(ex).isNotNull();
        then(fileStoragePort).shouldHaveNoInteractions();
    }
}
