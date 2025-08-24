package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
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

    @Mock private FindDownloadDataFileUrlPort findDownloadDataFileUrlPort;
    @Mock private UpdateDataDownloadPort updateDataDownloadPort;
    @Mock private ManageDataProjectionTaskPort manageDataProjectionTaskPort;
    @Mock private DownloadFileUseCase downloadFileUseCase;

    @Test
    void downloadDataFileShouldReturnPresignedUrl() {
        given(findDownloadDataFileUrlPort.findDownloadedDataFileUrl(1L))
                .willReturn(Optional.of("s3://file"));
        given(downloadFileUseCase.generatePreSignedUrl(any(), anyInt()))
                .willReturn(new GetPreSignedUrlResponse("http://signed"));

        GetDataPreSignedUrlResponse res = service.downloadDataFile(1L, 60);

        assertThat(res.preSignedUrl()).contains("http://signed");
        then(updateDataDownloadPort).should().increaseDownloadCount(1L);
        then(manageDataProjectionTaskPort).should().enqueueDownloadDelta(1L, 1);
    }

    @Test
    void downloadDataFileShouldThrowWhenNotFound() {
        given(findDownloadDataFileUrlPort.findDownloadedDataFileUrl(1L)).willReturn(Optional.empty());

        DataException ex = catchThrowableOfType(() ->
                service.downloadDataFile(1L, 60), DataException.class);

        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void downloadDataFileShouldThrowWhenPresignedFails() {
        given(findDownloadDataFileUrlPort.findDownloadedDataFileUrl(1L))
                .willReturn(Optional.of("s3://file"));
        given(downloadFileUseCase.generatePreSignedUrl(any(), anyInt()))
                .willThrow(new RuntimeException("boom"));

        DataException ex = catchThrowableOfType(() ->
                service.downloadDataFile(1L, 60), DataException.class);

        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.DOWNLOAD_URL_GENERATION_FAILED);
    }
}
