package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.mapper.command.UploadedDataDtoMapper;
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
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final UploadedDataDtoMapper uploadedDataDtoMapper;

    private final CreateDataPort createDataPort;
    private final UpdateDataPort updateDataPort;

    private final UpdateDataFilePort updateDataFilePort;
    private final UpdateThumbnailFilePort updateThumbnailFilePort;
    private final DataUploadEventPort dataUploadEventPort;

    private final CheckDataExistsByIdPort checkDataExistsByIdPort;
    private final FindDataPort findDataPort;

    private final FileUploadUseCase fileUploadUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;
    private final ValidateDataSourceUseCase validateDataSourceUseCase;
    private final ValidateDataTypeUseCase validateDataTypeUseCase;

    @Value("${default.image.url:}")
    private String defaultImageUrl;

    /**
     * 데이터셋 파일과 썸네일 파일의 유효성을 검증하고 업로드한 뒤, 데이터셋 정보를 저장하고 업로드 이벤트를 발행합니다.
     *
     * 데이터셋 메타데이터와 파일의 유효성을 검사하며, 주제, 데이터소스, 데이터유형 ID의 존재 여부도 확인합니다. 데이터셋 정보는 저장 후 파일 업로드가 성공하면 해당 URL로 갱신됩니다. 파일 업로드 중 오류 발생 시 트랜잭션이 롤백됩니다. 데이터셋 파일 업로드가 완료되면 업로드 이벤트가 발행됩니다.
     *
     * @param userId 데이터셋을 업로드하는 사용자 ID
     * @param dataFile 업로드할 데이터셋 파일
     * @param thumbnailFile 업로드할 썸네일 파일
     * @param requestDto 데이터셋 메타데이터 요청 정보
     * @return 저장된 데이터셋의 ID
     */
    @Override
    @Transactional
    public Long uploadData(Long userId, MultipartFile dataFile, MultipartFile thumbnailFile, UploadDataRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("UploadDataUseCase", "데이터셋 업로드 서비스 시작 title=" + requestDto.title());

        // 유효성 검사
        validateDataRequest(
                dataFile,
                thumbnailFile,
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.topicId(),
                requestDto.dataSourceId(),
                requestDto.dataTypeId(),
                "UploadDataUseCase"
        );

        // 데이터셋 저장
        Data data = uploadedDataDtoMapper.toDomain(
                requestDto,
                userId,
                defaultImageUrl
        );
        Data saveData = createDataPort.saveData(data);

        // 데이터셋 파일
        dataFileUpload(dataFile, saveData.getId(), "UploadDataUseCase");
        thumbnailFileUpload(thumbnailFile, saveData.getId(), "UploadDataUseCase");

        Data updatedFileUrlData = findDataPort.findDataById(saveData.getId()).get();

        // 데이터셋 파일 파싱 후 통계 저장
        if (dataFile != null && !dataFile.isEmpty()) {
            dataUploadEventPort.sendUploadEvent(updatedFileUrlData.getId(), updatedFileUrlData.getDataFileUrl(), dataFile.getOriginalFilename());
        }

        LoggerFactory.service().logSuccess("UploadDataUseCase", "데이터셋 업로드 서비스 종료 title=" + requestDto.title(), startTime);
        return saveData.getId();
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
        Instant startTime = LoggerFactory.service().logStart("ModifyDataUseCase", "데이터셋 수정 서비스 시작 dataId=" + dataId);

        if (!checkDataExistsByIdPort.existsDataById(dataId)) {
            LoggerFactory.service().logWarning("ModifyDataUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
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
                "ModifyDataUseCase"
        );

        updateDataPort.modifyData(dataId, requestDto);

        // 데이터셋 파일
        dataFileUpload(dataFile, dataId, "ModifyDataUseCase");
        thumbnailFileUpload(thumbnailFile, dataId, "ModifyDataUseCase");

        Data savedData = findDataPort.findDataById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("ModifyDataUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });

        // 데이터셋 파일 파싱 후 통계 저장
        if (dataFile != null && !dataFile.isEmpty()) {
            dataUploadEventPort.sendUploadEvent(savedData.getId(), savedData.getDataFileUrl(), dataFile.getOriginalFilename());
        }

        LoggerFactory.service().logSuccess("ModifyDataUseCase", "데이터셋 수정 서비스 종료 dataId=" + dataId, startTime);
    }

    /**
     * 데이터셋 파일, 썸네일 파일, 날짜, 주제/데이터소스/데이터타입 ID의 유효성을 검증합니다.
     *
     * 시작일이 종료일보다 늦을 경우 예외를 발생시키며, 파일 형식 및 각 ID의 존재 여부를 확인합니다.
     *
     * @throws DataException 시작일이 종료일보다 늦거나 유효하지 않은 값이 입력된 경우 발생합니다.
     */
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
     * 지정된 데이터셋 파일을 스토리지에 업로드하고, 해당 데이터셋의 파일 URL을 갱신합니다.
     *
     * 파일 업로드 중 오류가 발생하면 트랜잭션 롤백을 위해 런타임 예외를 발생시킵니다.
     *
     * @param dataFile 업로드할 데이터셋 파일
     * @param dataId   파일을 연결할 데이터셋의 식별자
     * @param useCase  호출한 서비스 또는 유스케이스 식별자
     */
    private void dataFileUpload(MultipartFile dataFile, Long dataId, String useCase) {
        // 데이터셋 파일 업로드 시도
        if (dataFile != null && !dataFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("data", dataId, dataFile.getOriginalFilename());
                String dataFileUrl = fileUploadUseCase.uploadFile(key, dataFile);
                updateDataFilePort.updateDataFile(dataId, dataFileUrl);
            } catch (Exception e) {
                LoggerFactory.service().logException(useCase, "데이터셋 파일 업로드 실패. fileName=" + dataFile.getOriginalFilename(), e);
                throw new RuntimeException("데이터셋 파일 업로드 실패", e);
            }
        }
    }

    /**
     * 데이터셋의 썸네일 파일을 스토리지에 업로드하고 데이터셋 레코드의 썸네일 URL을 갱신합니다.
     *
     * @param thumbnailFile 업로드할 썸네일 파일
     * @param dataId        썸네일을 등록할 데이터셋의 ID
     * @param useCase       로깅 및 예외 메시지에 사용할 업무 구분 문자열
     * @throws RuntimeException 파일 업로드에 실패할 경우 트랜잭션 롤백을 위해 발생
     */
    private void thumbnailFileUpload(MultipartFile thumbnailFile, Long dataId, String useCase) {
        // 썸네일 파일 업로드 시도
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateThumbnailKey("data", dataId, thumbnailFile.getOriginalFilename());
                String thumbnailFileUrl = fileUploadUseCase.uploadFile(key, thumbnailFile);
                updateThumbnailFilePort.updateThumbnailFile(dataId, thumbnailFileUrl);
            } catch (Exception e) {
                LoggerFactory.service().logException(useCase, "데이터셋 썸네일 파일 업로드 실패. fileName=" + thumbnailFile.getOriginalFilename(), e);
                throw new RuntimeException("썸네일 파일 업로드 실패", e); // rollback 유도
            }
        }
    }
}
