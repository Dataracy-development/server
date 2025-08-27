package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataDownloadServiceTest {

    @InjectMocks
    private DataDownloadService service;

    @Mock
    private FindDownloadDataFileUrlPort findDownloadDataFileUrlPort;

    @Mock
    private UpdateDataDownloadPort updateDataDownloadPort;

    @Mock
    private ManageDataProjectionTaskPort manageDataProjectionTaskPort;

    @Mock
    private DownloadFileUseCase downloadFileUseCase;

    @Test
    @DisplayName("데이터 파일 다운로드 성공 → PreSignedUrl 반환 및 다운로드 카운트 증가 + ProjectionTask 등록")
    void downloadDataFileShouldReturnPresignedUrl() {
        // given
        given(findDownloadDataFileUrlPort.findDownloadedDataFileUrl(1L))
                .willReturn(Optional.of("s3://file"));
        given(downloadFileUseCase.generatePreSignedUrl(any(), anyInt()))
                .willReturn(new GetPreSignedUrlResponse("http://signed"));

        // when
        GetDataPreSignedUrlResponse res = service.downloadDataFile(1L, 60);

        // then
        assertThat(res.preSignedUrl()).contains("http://signed");
        then(updateDataDownloadPort).should().increaseDownloadCount(1L);
        then(manageDataProjectionTaskPort).should().enqueueDownloadDelta(1L, 1);
    }

    @Test
    @DisplayName("데이터 파일 다운로드 실패 → 데이터 없음 시 NOT_FOUND_DATA 예외 발생")
    void downloadDataFileShouldThrowWhenNotFound() {
        // given
        given(findDownloadDataFileUrlPort.findDownloadedDataFileUrl(1L))
                .willReturn(Optional.empty());

        // when & then
        DataException ex = catchThrowableOfType(
                () -> service.downloadDataFile(1L, 60),
                DataException.class
        );
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("데이터 파일 다운로드 실패 → PreSignedUrl 생성 실패 시 DOWNLOAD_URL_GENERATION_FAILED 예외 발생")
    void downloadDataFileShouldThrowWhenPresignedFails() {
        // given
        given(findDownloadDataFileUrlPort.findDownloadedDataFileUrl(1L))
                .willReturn(Optional.of("s3://file"));
        given(downloadFileUseCase.generatePreSignedUrl(any(), anyInt()))
                .willThrow(new RuntimeException("boom"));

        // when & then
        DataException ex = catchThrowableOfType(
                () -> service.downloadDataFile(1L, 60),
                DataException.class
        );
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.DOWNLOAD_URL_GENERATION_FAILED);
    }
}
