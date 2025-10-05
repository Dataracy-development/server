package com.dataracy.modules.dataset.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
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
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataCommandServiceTest {

  // Test constants
  private static final Long FIVE_HUNDRED_TWELVE = 512L;

  @InjectMocks private DataCommandService service;

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

  private Data createSampleData() {
    return Data.of(
        1L,
        "Test Data",
        1L,
        1L,
        1L,
        1L,
        LocalDate.now(),
        LocalDate.now(),
        "Description",
        "Guide",
        "file-url",
        "thumb-url",
        1,
        1024L,
        DataMetadata.of(1L, 10, 5, "{}"),
        LocalDateTime.now());
  }

  private UploadDataRequest createSampleUploadRequest() {
    return new UploadDataRequest(
        "Test Data",
        1L,
        2L,
        3L,
        LocalDate.now(),
        LocalDate.now().plusDays(1),
        "Description",
        "Guide");
  }

  private ModifyDataRequest createSampleModifyRequest() {
    return new ModifyDataRequest(
        "Modified Data",
        1L,
        2L,
        3L,
        LocalDate.now(),
        LocalDate.now().plusDays(1),
        "Modified Description",
        "Modified Guide");
  }

  @Nested
  @DisplayName("데이터 업로드")
  class UploadData {

    @Test
    @DisplayName("데이터 업로드 성공")
    void uploadDataSuccess() {
      // given
      Long userId = 1L;
      UploadDataRequest request = createSampleUploadRequest();
      Data data = createSampleData();
      Data savedData = createSampleData();

      // Mock MultipartFile 설정
      given(dataFile.isEmpty()).willReturn(false);
      given(dataFile.getOriginalFilename()).willReturn("test.csv");
      given(dataFile.getSize()).willReturn(1024L);
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(FIVE_HUNDRED_TWELVE);

      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(validateDataSourceUseCase).validateDataSource(anyLong());
      willDoNothing().given(validateDataTypeUseCase).validateDataType(anyLong());
      given(createDataDtoMapper.toDomain(any(), anyLong())).willReturn(data);
      given(createDataPort.saveData(any())).willReturn(savedData);
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");
      given(findDataPort.findDataById(anyLong())).willReturn(Optional.of(savedData));

      // when
      UploadDataResponse response = service.uploadData(userId, dataFile, thumbnailFile, request);

      // then
      assertThat(response.id()).isEqualTo(savedData.getId());
      then(createDataPort).should().saveData(any());
      then(fileCommandUseCase).should(times(2)).uploadFile(anyString(), any());
      then(dataUploadEventPort).should().sendUploadEvent(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("데이터 업로드 실패 - 시작일이 종료일보다 늦음")
    void uploadDataFailInvalidDateRange() {
      // given
      Long userId = 1L;
      UploadDataRequest request =
          new UploadDataRequest(
              "Test Data",
              1L,
              2L,
              3L,
              LocalDate.now().plusDays(1), // 시작일이 종료일보다 늦음
              LocalDate.now(),
              "Description",
              "Guide");

      // when & then
      DataException ex =
          catchThrowableOfType(
              () -> service.uploadData(userId, dataFile, thumbnailFile, request),
              DataException.class);
      assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.BAD_REQUEST_DATE);
    }

    @Test
    @DisplayName("데이터 업로드 실패 - 파일 업로드 실패")
    void uploadDataFailFileUploadFailure() {
      // given
      Long userId = 1L;
      UploadDataRequest request = createSampleUploadRequest();
      Data data = createSampleData();
      Data savedData = createSampleData();

      // Mock MultipartFile 설정
      given(dataFile.isEmpty()).willReturn(false);
      given(dataFile.getOriginalFilename()).willReturn("test.csv");
      given(dataFile.getSize()).willReturn(1024L);

      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(validateDataSourceUseCase).validateDataSource(anyLong());
      willDoNothing().given(validateDataTypeUseCase).validateDataType(anyLong());
      given(createDataDtoMapper.toDomain(any(), anyLong())).willReturn(data);
      given(createDataPort.saveData(any())).willReturn(savedData);
      given(fileCommandUseCase.uploadFile(anyString(), any()))
          .willThrow(new RuntimeException("Upload failed"));

      // when & then
      CommonException ex =
          catchThrowableOfType(
              () -> service.uploadData(userId, dataFile, thumbnailFile, request),
              CommonException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST_IMAGE_FILE_TYPE);
    }

    @Test
    @DisplayName("데이터 업로드 성공 - null 파일들")
    void uploadDataSuccessWithNullFiles() {
      // given
      Long userId = 1L;
      UploadDataRequest request = createSampleUploadRequest();
      Data data = createSampleData();
      Data savedData = createSampleData();

      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(validateDataSourceUseCase).validateDataSource(anyLong());
      willDoNothing().given(validateDataTypeUseCase).validateDataType(anyLong());
      given(createDataDtoMapper.toDomain(any(), anyLong())).willReturn(data);
      given(createDataPort.saveData(any())).willReturn(savedData);
      given(findDataPort.findDataById(anyLong())).willReturn(Optional.of(savedData));

      // when
      UploadDataResponse response = service.uploadData(userId, null, null, request);

      // then
      assertThat(response.id()).isEqualTo(savedData.getId());
      then(fileCommandUseCase).should(never()).uploadFile(anyString(), any());
      then(dataUploadEventPort)
          .should(never())
          .sendUploadEvent(anyLong(), anyString(), anyString());
    }
  }

  @Nested
  @DisplayName("데이터 수정")
  class ModifyData {

    @Test
    @DisplayName("데이터 수정 성공")
    void modifyDataSuccess() {
      // given
      Long dataId = 1L;
      ModifyDataRequest request = createSampleModifyRequest();
      Data savedData = createSampleData();

      // Mock MultipartFile 설정
      given(dataFile.isEmpty()).willReturn(false);
      given(dataFile.getOriginalFilename()).willReturn("modified.csv");
      given(dataFile.getSize()).willReturn(2048L);
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("modified.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(true);
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(validateDataSourceUseCase).validateDataSource(anyLong());
      willDoNothing().given(validateDataTypeUseCase).validateDataType(anyLong());
      willDoNothing().given(updateDataPort).modifyData(anyLong(), any());
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");
      given(findDataPort.findDataById(dataId)).willReturn(Optional.of(savedData));

      // when
      service.modifyData(dataId, dataFile, thumbnailFile, request);

      // then
      then(updateDataPort).should().modifyData(dataId, request);
      then(fileCommandUseCase).should(times(2)).uploadFile(anyString(), any());
      then(dataUploadEventPort).should().sendUploadEvent(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("데이터 수정 실패 - 데이터가 존재하지 않음")
    void modifyDataFailDataNotFound() {
      // given
      Long dataId = 999L;
      ModifyDataRequest request = createSampleModifyRequest();

      given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(false);

      // when & then
      DataException ex =
          catchThrowableOfType(
              () -> service.modifyData(dataId, dataFile, thumbnailFile, request),
              DataException.class);
      assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("데이터 수정 실패 - 파일 업로드 실패")
    void modifyDataFailFileUploadFailure() {
      // given
      Long dataId = 1L;
      ModifyDataRequest request = createSampleModifyRequest();

      // Mock MultipartFile 설정
      given(dataFile.isEmpty()).willReturn(false);
      given(dataFile.getOriginalFilename()).willReturn("modified.csv");
      given(dataFile.getSize()).willReturn(2048L);

      given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(true);
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(validateDataSourceUseCase).validateDataSource(anyLong());
      willDoNothing().given(validateDataTypeUseCase).validateDataType(anyLong());
      willDoNothing().given(updateDataPort).modifyData(anyLong(), any());
      given(fileCommandUseCase.uploadFile(anyString(), any()))
          .willThrow(new RuntimeException("Upload failed"));

      // when & then
      CommonException ex =
          catchThrowableOfType(
              () -> service.modifyData(dataId, dataFile, thumbnailFile, request),
              CommonException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST_IMAGE_FILE_TYPE);
    }
  }

  @Nested
  @DisplayName("경계값 테스트")
  class BoundaryTests {

    @Test
    @DisplayName("빈 데이터 파일로 업로드")
    void uploadDataWithEmptyDataFile() {
      // given
      Long userId = 1L;
      UploadDataRequest request = createSampleUploadRequest();
      Data data = createSampleData();
      Data savedData = createSampleData();

      // Mock MultipartFile 설정 - 빈 파일
      given(dataFile.isEmpty()).willReturn(true);
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(FIVE_HUNDRED_TWELVE);

      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(validateDataSourceUseCase).validateDataSource(anyLong());
      willDoNothing().given(validateDataTypeUseCase).validateDataType(anyLong());
      given(createDataDtoMapper.toDomain(any(), anyLong())).willReturn(data);
      given(createDataPort.saveData(any())).willReturn(savedData);
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");
      given(findDataPort.findDataById(anyLong())).willReturn(Optional.of(savedData));

      // when
      UploadDataResponse response = service.uploadData(userId, dataFile, thumbnailFile, request);

      // then
      assertThat(response.id()).isEqualTo(savedData.getId());
      then(fileCommandUseCase).should(times(1)).uploadFile(anyString(), any()); // 썸네일만 업로드
      then(dataUploadEventPort)
          .should(never())
          .sendUploadEvent(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("음수 데이터 ID로 수정")
    void modifyDataWithNegativeId() {
      // given
      Long negativeDataId = -1L;
      ModifyDataRequest request = createSampleModifyRequest();

      given(checkDataExistsByIdPort.existsDataById(negativeDataId)).willReturn(false);

      // when & then
      DataException ex =
          catchThrowableOfType(
              () -> service.modifyData(negativeDataId, dataFile, thumbnailFile, request),
              DataException.class);
      assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
  }
}
