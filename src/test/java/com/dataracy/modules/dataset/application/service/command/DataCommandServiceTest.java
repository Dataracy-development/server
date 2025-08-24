package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;
import com.dataracy.modules.dataset.application.mapper.command.CreateDataDtoMapper;
import com.dataracy.modules.dataset.application.port.out.command.create.CreateDataPort;
import com.dataracy.modules.dataset.application.port.out.command.event.DataUploadEventPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataFilePort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateThumbnailFilePort;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataCommandServiceTest {

    @InjectMocks
    private DataCommandService service;

    @Mock private CreateDataDtoMapper createDataDtoMapper;
    @Mock private CreateDataPort createDataPort;
    @Mock private UpdateDataPort updateDataPort;
    @Mock private UpdateDataFilePort updateDataFilePort;
    @Mock private UpdateThumbnailFilePort updateThumbnailFilePort;
    @Mock private DataUploadEventPort dataUploadEventPort;
    @Mock private CheckDataExistsByIdPort checkDataExistsByIdPort;
    @Mock private FindDataPort findDataPort;
    @Mock private FileCommandUseCase fileCommandUseCase;
    @Mock private ValidateTopicUseCase validateTopicUseCase;
    @Mock private ValidateDataSourceUseCase validateDataSourceUseCase;
    @Mock private ValidateDataTypeUseCase validateDataTypeUseCase;
    @Mock private MultipartFile dataFile;
    @Mock private MultipartFile thumbnailFile;

    @Mock private UploadDataRequest uploadDataRequest;
    @Mock private ModifyDataRequest modifyDataRequest;

    @BeforeEach
    void setup() {
        uploadDataRequest = new UploadDataRequest("title", 1L, 2L, 3L,
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "desc", "guide");

        modifyDataRequest = new ModifyDataRequest("title", 1L, 2L, 3L,
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "desc", "guide");
    }

    @Test
    void uploadDataShouldSaveAndReturnId() {
        // given
        Data mockData = mock(Data.class);
        MultipartFile dataFile = mock(MultipartFile.class);
        MultipartFile thumbnailFile = mock(MultipartFile.class);

        given(createDataDtoMapper.toDomain(any(), any())).willReturn(mockData);
        given(createDataPort.saveData(any())).willReturn(mockData);
        given(mockData.getId()).willReturn(1L);
        given(findDataPort.findDataById(any())).willReturn(Optional.of(mockData));

        // 파일 관련 stubbing
        given(dataFile.isEmpty()).willReturn(true);       // 데이터 파일 없음
        given(thumbnailFile.isEmpty()).willReturn(true);  // 썸네일 파일 없음

        // when
        UploadDataResponse res = service.uploadData(99L, dataFile, thumbnailFile, uploadDataRequest);

        // then
        assertThat(res.id()).isEqualTo(1L);
        then(createDataPort).should().saveData(mockData);
    }

    @Test
    void modifyDataShouldUpdateMetadataAndFiles() {
        // given
        Long dataId = 1L;
        MultipartFile dataFile = mock(MultipartFile.class);
        MultipartFile thumbnailFile = mock(MultipartFile.class);

        Data mockData = mock(Data.class);

        // 존재하는 데이터셋
        given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(true);

        // 파일 stubbing
        given(dataFile.isEmpty()).willReturn(false); // 새 데이터 파일 있음
        given(dataFile.getOriginalFilename()).willReturn("data.csv");
        given(thumbnailFile.isEmpty()).willReturn(true); // 새 썸네일 없음

        // DB 조회
        given(findDataPort.findDataById(dataId)).willReturn(Optional.of(mockData));
        given(mockData.getId()).willReturn(dataId);
        given(mockData.getDataFileUrl()).willReturn("s3://bucket/data.csv");

        // when
        service.modifyData(dataId, dataFile, thumbnailFile, modifyDataRequest);

        // then
        then(updateDataPort).should().modifyData(dataId, modifyDataRequest);
        then(findDataPort).should().findDataById(dataId);
        then(dataUploadEventPort).should()
                .sendUploadEvent(dataId, "s3://bucket/data.csv", "data.csv");
    }



    @Test
    void modifyDataShouldThrowWhenNotExists() {
        given(checkDataExistsByIdPort.existsDataById(1L)).willReturn(false);

        DataException ex = catchThrowableOfType(() ->
                service.modifyData(1L, dataFile, thumbnailFile, modifyDataRequest), DataException.class);

        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
}
