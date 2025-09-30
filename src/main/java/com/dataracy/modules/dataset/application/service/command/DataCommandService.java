package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;
import com.dataracy.modules.dataset.application.mapper.command.CreateDataDtoMapper;
import com.dataracy.modules.dataset.application.port.in.command.content.ModifyDataUseCase;
import com.dataracy.modules.dataset.application.port.in.command.content.UploadDataUseCase;
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
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DataCommandService implements
        UploadDataUseCase,
        ModifyDataUseCase
{
    private final CreateDataDtoMapper createDataDtoMapper;

    private final CreateDataPort createDataPort;
    private final UpdateDataPort updateDataPort;

    private final UpdateDataFilePort updateDataFilePort;
    private final UpdateThumbnailFilePort updateThumbnailFilePort;
    private final DataUploadEventPort dataUploadEventPort;

    private final CheckDataExistsByIdPort checkDataExistsByIdPort;

    // Use Case 상수 정의
    private static final String UPLOAD_DATA_USE_CASE = "UploadDataUseCase";
    private static final String MODIFY_DATA_USE_CASE = "ModifyDataUseCase";
    
    // 메시지 상수 정의
    private static final String DATA_NOT_FOUND_MESSAGE = "해당 데이터셋이 존재하지 않습니다. dataId=";
    private static final String DATA_NOT_FOUND_AFTER_FILE_UPLOAD_MESSAGE = "파일 업로드 후 데이터를 찾을 수 없습니다. dataId=";
    private final FindDataPort findDataPort;

    private final FileCommandUseCase fileCommandUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;
    private final ValidateDataSourceUseCase validateDataSourceUseCase;
    private final ValidateDataTypeUseCase validateDataTypeUseCase;

    /**
     * 데이터셋 메타데이터와 첨부 파일(데이터 파일, 썸네일)의 유효성을 검증하고 저장한 뒤 업로드 이벤트를 발행합니다.
     *
     * 요청한 메타데이터를 저장하고(트랜잭션 내), 파일을 스토리지에 업로드하여 저장된 데이터셋의 파일 URL을 갱신합니다. 데이터 파일이 존재하면 업로드 완료 이벤트를 발행합니다.
     *
     * @param userId 업로드를 요청한 사용자 ID
     * @param dataFile 업로드할 데이터셋 파일(없을 수 있음)
     * @param thumbnailFile 업로드할 썸네일 이미지 파일(없을 수 있음)
     * @param requestDto 데이터셋 생성에 필요한 메타데이터 요청 객체
     * @return 업로드된 데이터셋의 식별자(ID)를 포함한 {@link UploadDataResponse}
     */
    @Override
    @Transactional
    public UploadDataResponse uploadData(Long userId, MultipartFile dataFile, MultipartFile thumbnailFile, UploadDataRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart(UPLOAD_DATA_USE_CASE, "데이터셋 업로드 서비스 시작 title=" + requestDto.title());

        // 유효성 검사
        validateDataRequest(
                dataFile,
                thumbnailFile,
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.topicId(),
                requestDto.dataSourceId(),
                requestDto.dataTypeId(),
                UPLOAD_DATA_USE_CASE
        );

        // 데이터셋 저장
        Data data = createDataDtoMapper.toDomain(
                requestDto,
                userId
        );
        Data saveData = createDataPort.saveData(data);

        // 데이터셋 파일
        dataFileUpload(dataFile, saveData.getId(), UPLOAD_DATA_USE_CASE);
        thumbnailFileUpload(thumbnailFile, saveData.getId(), UPLOAD_DATA_USE_CASE);

        Data updatedFileUrlData = findDataPort.findDataById(saveData.getId())
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(UPLOAD_DATA_USE_CASE, DATA_NOT_FOUND_AFTER_FILE_UPLOAD_MESSAGE + saveData.getId());
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });

        // 데이터셋 파일 파싱 후 통계 저장
        if (dataFile != null && !dataFile.isEmpty()) {
            dataUploadEventPort.sendUploadEvent(updatedFileUrlData.getId(), updatedFileUrlData.getDataFileUrl(), dataFile.getOriginalFilename());
        }

        LoggerFactory.service().logSuccess(UPLOAD_DATA_USE_CASE, "데이터셋 업로드 서비스 종료 title=" + requestDto.title(), startTime);
        return new UploadDataResponse(saveData.getId());
    }


    /**
     * 데이터셋의 메타데이터와 파일(데이터셋 파일, 썸네일 파일)을 수정합니다.
     *
     * 데이터셋의 제목, 설명, 날짜 범위, 주제, 데이터 소스, 데이터 타입 등 메타데이터를 갱신하고,
     * 새로운 데이터셋 파일 또는 썸네일 파일이 제공된 경우 파일을 업로드하여 해당 URL로 갱신합니다.
     * 데이터셋이 존재하지 않으면 예외를 발생시킵니다.
     * 파일 업로드 실패 시 트랜잭션이 롤백되며, 데이터셋 파일이 수정된 경우 업로드 이벤트가 발행됩니다.
     *
     * @param dataId        수정할 데이터셋의 ID
     * @param dataFile      새 데이터셋 파일 (선택)
     * @param thumbnailFile 새 썸네일 파일 (선택)
     * @param requestDto    데이터셋 수정 요청 정보
     */
    @Override
    @Transactional
    public void modifyData(Long dataId, MultipartFile dataFile, MultipartFile thumbnailFile, ModifyDataRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart(MODIFY_DATA_USE_CASE, "데이터셋 수정 서비스 시작 dataId=" + dataId);

        if (!checkDataExistsByIdPort.existsDataById(dataId)) {
            LoggerFactory.service().logWarning(MODIFY_DATA_USE_CASE, DATA_NOT_FOUND_MESSAGE + dataId);
            throw new DataException(DataErrorStatus.NOT_FOUND_DATA);
        }

        // 유효성 검사
        validateDataRequest(
                dataFile,
                thumbnailFile,
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.topicId(),
                requestDto.dataSourceId(),
                requestDto.dataTypeId(),
                MODIFY_DATA_USE_CASE
        );

        updateDataPort.modifyData(dataId, requestDto);

        // 데이터셋 파일
        dataFileUpload(dataFile, dataId, MODIFY_DATA_USE_CASE);
        thumbnailFileUpload(thumbnailFile, dataId, MODIFY_DATA_USE_CASE);

        Data savedData = findDataPort.findDataById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(MODIFY_DATA_USE_CASE, DATA_NOT_FOUND_MESSAGE + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });

        // 데이터셋 파일 파싱 후 통계 저장
        if (dataFile != null && !dataFile.isEmpty()) {
            dataUploadEventPort.sendUploadEvent(savedData.getId(), savedData.getDataFileUrl(), dataFile.getOriginalFilename());
        }

        LoggerFactory.service().logSuccess(MODIFY_DATA_USE_CASE, "데이터셋 수정 서비스 종료 dataId=" + dataId, startTime);
    }

    /**
     * 데이터셋 파일, 썸네일 파일, 날짜, 주제/데이터소스/데이터타입 ID의 유효성을 검증합니다.
     * 시작일이 종료일보다 늦을 경우 예외를 발생시키며, 파일 형식 및 각 ID의 존재 여부를 확인합니다.
     * 
     * 참고: 8개의 파라미터를 가지지만, 데이터 업로드/수정 시 필요한 모든 검증을 수행하는 통합 검증 메서드입니다.
     *
     * @throws DataException 시작일이 종료일보다 늦거나 유효하지 않은 값이 입력된 경우 발생합니다.
     */
    @SuppressWarnings("java:S107") // 통합 검증 메서드로 여러 파라미터 필요
    private void validateDataRequest(
            MultipartFile dataFile,
            MultipartFile thumbnailFile,
            LocalDate startDate,
            LocalDate endDate,
            Long topicId,
            Long dataSourceId,
            Long dataTypeId,
            String useCase
    ) {
        if (startDate.isAfter(endDate)) {
            LoggerFactory.service().logWarning(useCase, "시작일이 종료일보다 늦습니다. startDate=" + startDate + ", endDate=" + endDate);
            throw new DataException(DataErrorStatus.BAD_REQUEST_DATE);
        }

        // 데이터셋 파일 유효성 검사
        FileUtil.validateGeneralFile(dataFile);
        // 썸네일 파일 유효성 검사
        FileUtil.validateImageFile(thumbnailFile);

        // 유효성 검사
        validateTopicUseCase.validateTopic(topicId);
        validateDataSourceUseCase.validateDataSource(dataSourceId);
        validateDataTypeUseCase.validateDataType(dataTypeId);
    }

    /**
         * 데이터 파일을 스토리지에 업로드하고 업로드된 URL과 파일 크기로 데이터 레코드를 갱신합니다.
         *
         * 업로드 중 오류가 발생하면 로그를 남기고 RuntimeException을 던져 트랜잭션을 롤백합니다.
         *
         * @param dataFile 업로드할 데이터 파일(MultipartFile, 비어있거나 null이면 아무 작업도 수행하지 않음)
         * @param dataId 파일을 연결할 데이터셋의 식별자
         * @param useCase 호출한 서비스/유스케이스를 식별하는 문자열(로그용)
         */
    private void dataFileUpload(MultipartFile dataFile, Long dataId, String useCase) {
        // 데이터셋 파일 업로드 시도
        if (dataFile != null && !dataFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("data", dataId, dataFile.getOriginalFilename());
                String dataFileUrl = fileCommandUseCase.uploadFile(key, dataFile);
                updateDataFilePort.updateDataFile(dataId, dataFileUrl, dataFile.getSize());
            } catch (Exception e) {
                LoggerFactory.service().logException(useCase, "데이터셋 파일 업로드 실패. fileName=" + dataFile.getOriginalFilename(), e);
                throw new CommonException(CommonErrorStatus.FILE_UPLOAD_FAILURE);
            }
        }
    }

    /**
     * 썸네일 파일을 스토리지에 업로드하고 해당 데이터셋의 썸네일 URL을 갱신합니다.
     *
     * @param thumbnailFile 업로드할 썸네일 파일
     * @param dataId 썸네일을 등록할 데이터셋의 ID
     * @param useCase 로깅 및 예외 메시지에 사용할 업무 구분 문자열
     * @throws RuntimeException 파일 업로드에 실패할 경우 트랜잭션 롤백을 위해 발생합니다.
     */
    private void thumbnailFileUpload(MultipartFile thumbnailFile, Long dataId, String useCase) {
        // 썸네일 파일 업로드 시도
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateThumbnailKey("data", dataId, thumbnailFile.getOriginalFilename());
                String thumbnailFileUrl = fileCommandUseCase.uploadFile(key, thumbnailFile);
                updateThumbnailFilePort.updateThumbnailFile(dataId, thumbnailFileUrl);
            } catch (Exception e) {
                LoggerFactory.service().logException(useCase, "데이터셋 썸네일 파일 업로드 실패. fileName=" + thumbnailFile.getOriginalFilename(), e);
                throw new CommonException(CommonErrorStatus.FILE_UPLOAD_FAILURE); // rollback 유도
            }
        }
    }
}
