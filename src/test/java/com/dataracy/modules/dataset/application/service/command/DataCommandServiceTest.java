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
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DataCommandServiceTest {

    @InjectMocks
    private DataCommandService service;

    @Mock
    private CreateDataDtoMapper createDataDtoMapper;

    @Mock
    private CreateDataPort createDataPort;

    @Mock
    private UpdateDataPort updateDataPort;

    @Mock
    private UpdateDataFilePort updateDataFilePort;

    @Mock
    private UpdateThumbnailFilePort updateThumbnailFilePort;

    @Mock
    private DataUploadEventPort dataUploadEventPort;

    @Mock
    private CheckDataExistsByIdPort checkDataExistsByIdPort;

    @Mock
    private FindDataPort findDataPort;

    @Mock
    private FileCommandUseCase fileCommandUseCase;

    @Mock
    private ValidateTopicUseCase validateTopicUseCase;

    @Mock
    private ValidateDataSourceUseCase validateDataSourceUseCase;

    @Mock
    private ValidateDataTypeUseCase validateDataTypeUseCase;

    @Nested
    @DisplayName("uploadData 메서드 테스트")
    class UploadDataTest {

        @Test
        @DisplayName("데이터 업로드 성공")
        void uploadDataSuccess() {
            // given
            Long userId = 1L;
            UploadDataRequest request = new UploadDataRequest(
                    "테스트 데이터", 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), "테스트 설명", "분석 가이드"
            );
            MultipartFile dataFile = null;
            MultipartFile thumbnailFile = null;
            Data data = Data.of(1L, "테스트 데이터", 1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), 
                    "테스트 설명", "분석 가이드", null, null, 0, null, null, LocalDateTime.now());
            UploadDataResponse expectedResponse = new UploadDataResponse(1L);

            given(createDataDtoMapper.toDomain(request, userId)).willReturn(data);
            given(createDataPort.saveData(data)).willReturn(data);
            given(findDataPort.findDataById(1L)).willReturn(java.util.Optional.of(data));

            // when
            UploadDataResponse result = service.uploadData(userId, dataFile, thumbnailFile, request);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            then(createDataDtoMapper).should().toDomain(request, userId);
            then(createDataPort).should().saveData(data);
            then(findDataPort).should().findDataById(1L);
        }
    }

    @Nested
    @DisplayName("modifyData 메서드 테스트")
    class ModifyDataTest {

        @Test
        @DisplayName("데이터 수정 성공")
        void modifyDataSuccess() {
            // given
            Long dataId = 1L;
            ModifyDataRequest request = new ModifyDataRequest(
                    "수정된 데이터", 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), "수정된 설명", "수정된 분석 가이드"
            );
            MultipartFile dataFile = null;
            MultipartFile thumbnailFile = null;
            Data existingData = Data.of(dataId, "기존 데이터", 1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), 
                    "기존 설명", "기존 분석 가이드", null, null, 0, null, null, LocalDateTime.now());

            given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(true);
            given(findDataPort.findDataById(dataId)).willReturn(java.util.Optional.of(existingData));

            // when
            service.modifyData(dataId, dataFile, thumbnailFile, request);

            // then
            then(checkDataExistsByIdPort).should().existsDataById(dataId);
            then(findDataPort).should().findDataById(dataId);
            then(updateDataPort).should().modifyData(dataId, request);
        }

        @Test
        @DisplayName("데이터 수정 실패 - 존재하지 않는 데이터")
        void modifyDataFail_DataNotExists() {
            // given
            Long dataId = 999L;
            ModifyDataRequest request = new ModifyDataRequest(
                    "수정된 데이터", 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), "수정된 설명", "수정된 분석 가이드"
            );
            MultipartFile dataFile = null;
            MultipartFile thumbnailFile = null;

            given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> service.modifyData(dataId, dataFile, thumbnailFile, request))
                    .isInstanceOf(DataException.class)
                    .hasMessage(DataErrorStatus.NOT_FOUND_DATA.getMessage());

            then(checkDataExistsByIdPort).should().existsDataById(dataId);
            then(findDataPort).shouldHaveNoInteractions();
            then(updateDataPort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("uploadData 메서드 추가 테스트")
    class UploadDataAdditionalTest {

        @Test
        @DisplayName("데이터 업로드 - 파일이 없는 경우")
        void uploadDataWithNoFiles() {
            // given
            Long userId = 1L;
            UploadDataRequest request = new UploadDataRequest(
                    "테스트 데이터", 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), "테스트 설명", "분석 가이드"
            );
            MultipartFile dataFile = null;
            MultipartFile thumbnailFile = null;
            Data data = Data.of(1L, "테스트 데이터", 1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), 
                    "테스트 설명", "분석 가이드", null, null, 0, null, null, LocalDateTime.now());
            UploadDataResponse expectedResponse = new UploadDataResponse(1L);

            given(createDataDtoMapper.toDomain(request, userId)).willReturn(data);
            given(createDataPort.saveData(data)).willReturn(data);
            given(findDataPort.findDataById(1L)).willReturn(java.util.Optional.of(data));

            // when
            UploadDataResponse result = service.uploadData(userId, dataFile, thumbnailFile, request);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            then(createDataDtoMapper).should().toDomain(request, userId);
            then(createDataPort).should().saveData(data);
            then(findDataPort).should().findDataById(1L);
        }
    }

    @Nested
    @DisplayName("modifyData 메서드 추가 테스트")
    class ModifyDataAdditionalTest {

        @Test
        @DisplayName("데이터 수정 - 파일이 없는 경우")
        void modifyDataWithNoFiles() {
            // given
            Long dataId = 1L;
            ModifyDataRequest request = new ModifyDataRequest(
                    "수정된 데이터", 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), "수정된 설명", "수정된 분석 가이드"
            );
            MultipartFile dataFile = null;
            MultipartFile thumbnailFile = null;
            Data existingData = Data.of(dataId, "기존 데이터", 1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now(), 
                    "기존 설명", "기존 분석 가이드", null, null, 0, null, null, LocalDateTime.now());

            given(checkDataExistsByIdPort.existsDataById(dataId)).willReturn(true);
            given(findDataPort.findDataById(dataId)).willReturn(java.util.Optional.of(existingData));

            // when
            service.modifyData(dataId, dataFile, thumbnailFile, request);

            // then
            then(checkDataExistsByIdPort).should().existsDataById(dataId);
            then(findDataPort).should().findDataById(dataId);
            then(updateDataPort).should().modifyData(dataId, request);
        }
    }
}
