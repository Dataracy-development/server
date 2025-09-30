package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileDownloadServiceTest {

    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private FileDownloadService service;

    @Nested
    @DisplayName("일시적으로 파일 다운로드가 가능한 URL 반환")
    class GeneratePreSignedUrl {

        @Test
        @DisplayName("성공: 유효한 URL과 만료 시간이 주어지면 사전 서명 URL 반환")
        void success() {
            // given
            String s3Url = "https://bucket.s3.ap-northeast-2.amazonaws.com/path/to/file.jpg";
            int expirationSeconds = 300;
            given(fileStoragePort.getPreSignedUrl(s3Url, expirationSeconds))
                    .willReturn("https://signed");

            // when
            GetPreSignedUrlResponse response = service.generatePreSignedUrl(s3Url, expirationSeconds);

            // then
            assertThat(response).isNotNull();
            assertThat(response.preSignedUrl()).isEqualTo("https://signed");
            then(fileStoragePort).should().getPreSignedUrl(s3Url, expirationSeconds);
        }

        @Test
        @DisplayName("실패: URL이 공백이면 IllegalArgumentException 발생")
        void failWhenUrlBlank() {
            // given
            String s3Url = "   ";
            int expirationSeconds = 100;

            // when
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> service.generatePreSignedUrl(s3Url, expirationSeconds),
                    IllegalArgumentException.class
            );

            // then
            assertThat(ex).isNotNull();
            then(fileStoragePort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패: 만료 시간이 0 이하이면 IllegalArgumentException 발생")
        void failWhenExpirationNonPositive() {
            // given
            String s3Url = "https://bucket.s3.ap-northeast-2.amazonaws.com/path/to/file.jpg";
            int expirationSeconds = 0;

            // when
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> service.generatePreSignedUrl(s3Url, expirationSeconds),
                    IllegalArgumentException.class
            );

            // then
            assertThat(ex).isNotNull();
            then(fileStoragePort).shouldHaveNoInteractions();
        }
    }
}
