package com.dataracy.modules.dataset.adapter.web;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.api.command.DataCommandController;
import com.dataracy.modules.dataset.adapter.web.mapper.command.DataCommandWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.download.DataDownloadWebMapper;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.command.UploadDataWebResponse;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import com.dataracy.modules.dataset.application.port.in.command.content.DeleteDataUseCase;
import com.dataracy.modules.dataset.application.port.in.command.content.DownloadDataFileUseCase;
import com.dataracy.modules.dataset.application.port.in.command.content.RestoreDataUseCase;
import com.dataracy.modules.dataset.application.port.in.command.content.UploadDataUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class DataCommandControllerTest {

    @Mock private DataCommandWebMapper dataCommandWebMapper;
    @Mock private DataDownloadWebMapper dataDownloadWebMapper;
    @Mock private UploadDataUseCase uploadDataUseCase;
    @Mock private DeleteDataUseCase deleteDataUseCase;
    @Mock private RestoreDataUseCase restoreDataUseCase;
    @Mock private DownloadDataFileUseCase downloadDataFileUseCase;

    @InjectMocks private DataCommandController controller;

    @Test
    @DisplayName("데이터 업로드 성공 시 201 반환")
    void uploadDataShouldReturnCreated() {
        UploadDataWebRequest req = new UploadDataWebRequest("title", 1L, 2L, 3L, null, null, "desc", "guide");
        UploadDataResponse resDto = new UploadDataResponse(1L);
        UploadDataWebResponse webRes = new UploadDataWebResponse(1L);

        given(dataCommandWebMapper.toApplicationDto(req)).willReturn(null);
        given(uploadDataUseCase.uploadData(anyLong(), any(), any(), any())).willReturn(resDto);
        given(dataCommandWebMapper.toWebDto(resDto)).willReturn(webRes);

        ResponseEntity<SuccessResponse<UploadDataWebResponse>> response =
                controller.uploadData(1L, null, null, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("데이터 삭제 성공 시 200 반환")
    void deleteDataShouldReturnOk() {
        doNothing().when(deleteDataUseCase).deleteData(1L);

        ResponseEntity<SuccessResponse<Void>> response = controller.deleteData(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("데이터 복원 성공 시 200 반환")
    void restoreDataShouldReturnOk() {
        doNothing().when(restoreDataUseCase).restoreData(1L);

        ResponseEntity<SuccessResponse<Void>> response = controller.restoreData(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("프리사인드 다운로드 URL 조회 성공 시 200 반환")
    void getPreSignedDataUrlShouldReturnOk() {
        GetDataPreSignedUrlResponse resDto = new GetDataPreSignedUrlResponse("http://url");
        given(downloadDataFileUseCase.downloadDataFile(anyLong(), anyInt())).willReturn(resDto);
        given(dataDownloadWebMapper.toWebDto(resDto))
                .willReturn(new com.dataracy.modules.dataset.adapter.web.response.download.GetDataPreSignedUrlWebResponse("http://url"));

        ResponseEntity<?> response = controller.getPreSignedDataUrl(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

