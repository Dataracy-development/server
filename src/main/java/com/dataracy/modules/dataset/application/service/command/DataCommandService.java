package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.dataset.application.dto.request.DataModifyRequest;
import com.dataracy.modules.dataset.application.dto.request.DataUploadRequest;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataIndexingPort;
import com.dataracy.modules.dataset.application.port.in.DataDeleteUseCase;
import com.dataracy.modules.dataset.application.port.in.DataModifyUseCase;
import com.dataracy.modules.dataset.application.port.in.DataRestoreUseCase;
import com.dataracy.modules.dataset.application.port.in.DataUploadUseCase;
import com.dataracy.modules.dataset.application.port.out.DataKafkaProducerPort;
import com.dataracy.modules.dataset.application.port.out.DataRepositoryPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.ValidateDataTypeUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCommandService implements
        DataUploadUseCase,
        DataModifyUseCase,
        DataDeleteUseCase,
        DataRestoreUseCase
{
    private final DataRepositoryPort dataRepositoryPort;
    private final DataKafkaProducerPort kafkaProducerPort;
    private final DataIndexingPort dataIndexingPort;

    private final FileUploadUseCase fileUploadUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;
    private final ValidateDataSourceUseCase validateDataSourceUseCase;
    private final ValidateDataTypeUseCase validateDataTypeUseCase;

    @Value("${default.image.url:}")
    private String defaultImageUrl;

    /**
     * 데이터셋 파일과 썸네일 파일을 검증 및 업로드하고, 데이터셋 정보를 저장한 후 업로드 이벤트를 발행합니다.
     *
     * 데이터셋 메타데이터와 파일의 유효성을 검사하고, 주제/데이터소스/데이터유형 ID를 각각 검증합니다. 데이터셋 정보는 데이터베이스에 저장되며, 파일 업로드가 성공하면 해당 URL이 데이터셋에 반영됩니다. 파일 업로드 중 오류가 발생하면 트랜잭션이 롤백됩니다. 데이터셋 파일 업로드가 완료되면 업로드 이벤트가 발행됩니다.
     *
     * @param userId 데이터셋을 업로드하는 사용자 ID
     * @param dataFile 업로드할 데이터셋 파일
     * @param thumbnailFile 업로드할 썸네일 파일
     * @param requestDto 데이터셋 메타데이터 요청 정보
     * @return 저장된 데이터셋의 ID
     */
    @Override
    @Transactional
    public Long upload(Long userId, MultipartFile dataFile, MultipartFile thumbnailFile, DataUploadRequest requestDto) {
        log.info("데이터셋 업로드 시작 - userId: {}, title: {}", userId, requestDto.title());
        if (requestDto.startDate().isAfter(requestDto.endDate())) {
            throw new DataException(DataErrorStatus.BAD_REQUEST_DATE);
        }

        // 데이터셋 파일 유효성 검사
        FileUtil.validateGeneralFile(dataFile);
        // 썸네일 파일 유효성 검사
        FileUtil.validateImageFile(thumbnailFile);

        // 유효성 검사
        validateTopicUseCase.validateTopic(requestDto.topicId());
        validateDataSourceUseCase.validateDataSource(requestDto.dataSourceId());
        validateDataTypeUseCase.validateDataType(requestDto.dataTypeId());

        // 데이터셋 업로드 DB 저장
        Data data = Data.of(
                null,
                requestDto.title(),
                requestDto.topicId(),
                userId,
                requestDto.dataSourceId(),
                requestDto.dataTypeId(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                requestDto.analysisGuide(),
                null,
                defaultImageUrl,
                0,
                0,
                null,
                null
        );

        Data saveData = dataRepositoryPort.saveData(data);

        // 데이터셋 파일 업로드 시도
        if (dataFile != null && !dataFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("data", saveData.getId(), dataFile.getOriginalFilename());
                String dataFileUrl = fileUploadUseCase.uploadFile(key, dataFile);
                log.info("데이터셋 파일 업로드 성공 - url={}", dataFileUrl);

                saveData.updateDataFileUrl(dataFileUrl);
                dataRepositoryPort.updateDataFile(saveData.getId(), dataFileUrl);
            } catch (Exception e) {
                log.error("데이터셋 파일 업로드 실패. 프로젝트 ID={}, 에러={}", saveData.getId(), e.getMessage());
                throw new RuntimeException("데이터셋 파일 업로드 실패", e); // rollback 유도
            }
        }
        // 썸네일 파일 업로드 시도
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateThumbnailKey("data", saveData.getId(), thumbnailFile.getOriginalFilename());
                String thumbnailFileUrl = fileUploadUseCase.uploadFile(key, thumbnailFile);
                log.info("썸네일 파일 업로드 성공 - url={}", thumbnailFileUrl);

                saveData.updateThumbnailFileUrl(thumbnailFileUrl);
                dataRepositoryPort.updateThumbnailFile(saveData.getId(), thumbnailFileUrl);
            } catch (Exception e) {
                log.error("썸네일 파일 업로드 실패. 데이터 ID={}, 에러={}", saveData.getId(), e.getMessage());
                throw new RuntimeException("썸네일 파일 업로드 실패", e); // rollback 유도
            }
        }

        // 데이터셋 파일 파싱 후 통계 저장
        if (dataFile != null && !dataFile.isEmpty()) {
            kafkaProducerPort.sendUploadEvent(saveData.getId(), saveData.getDataFileUrl(), dataFile.getOriginalFilename());
        }

        log.info("데이터셋 업로드 완료 - userId: {}, title: {}", userId, requestDto.title());
        return saveData.getId();
    }

    /**
     * 데이터셋의 메타데이터와 파일(데이터셋 파일, 썸네일 파일)을 수정합니다.
     *
     * 데이터셋의 제목, 설명, 날짜 범위, 주제, 데이터 소스, 데이터 타입 등 메타데이터를 갱신하고,
     * 새로운 데이터셋 파일 또는 썸네일 파일이 제공된 경우 파일을 업로드하여 해당 URL로 갱신합니다.
     * 파일 업로드 실패 시 트랜잭션이 롤백됩니다.
     * 데이터셋 파일이 수정된 경우, 업로드 이벤트를 카프카로 발행합니다.
     *
     * @param dataId        수정할 데이터셋의 ID
     * @param dataFile      새 데이터셋 파일 (선택)
     * @param thumbnailFile 새 썸네일 파일 (선택)
     * @param requestDto    데이터셋 수정 요청 정보
     */
    @Override
    public void modify(Long dataId, MultipartFile dataFile, MultipartFile thumbnailFile, DataModifyRequest requestDto) {
        log.info("데이터셋 수정 시작 - dataId: {}, title: {}", dataId, requestDto.title());
        if (requestDto.startDate().isAfter(requestDto.endDate())) {
            throw new DataException(DataErrorStatus.BAD_REQUEST_DATE);
        }

        // 데이터셋 파일 유효성 검사
        FileUtil.validateGeneralFile(dataFile);
        // 썸네일 파일 유효성 검사
        FileUtil.validateImageFile(thumbnailFile);

        // 유효성 검사
        validateTopicUseCase.validateTopic(requestDto.topicId());
        validateDataSourceUseCase.validateDataSource(requestDto.dataSourceId());
        validateDataTypeUseCase.validateDataType(requestDto.dataTypeId());

        dataRepositoryPort.modify(dataId,requestDto);

        // 데이터셋 파일 업로드 시도
        if (dataFile != null && !dataFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("data", dataId, dataFile.getOriginalFilename());
                String dataFileUrl = fileUploadUseCase.uploadFile(key, dataFile);
                log.info("새 데이터셋 파일 업로드 성공 - url={}", dataFileUrl);

                dataRepositoryPort.updateDataFile(dataId, dataFileUrl);
            } catch (Exception e) {
                log.error("새 데이터셋 파일 업로드 실패. 데이터셋 ID={}, 에러={}", dataId, e.getMessage());
                throw new RuntimeException("새 데이터셋 파일 업로드 실패", e); // rollback 유도
            }
        }
        // 썸네일 파일 업로드 시도
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateThumbnailKey("data", dataId, thumbnailFile.getOriginalFilename());
                String thumbnailFileUrl = fileUploadUseCase.uploadFile(key, thumbnailFile);
                log.info("새 썸네일 파일 업로드 성공 - url={}", thumbnailFileUrl);

                dataRepositoryPort.updateThumbnailFile(dataId, thumbnailFileUrl);
            } catch (Exception e) {
                log.error("새 썸네일 파일 업로드 실패. 데이터 ID={}, 에러={}", dataId, e.getMessage());
                throw new RuntimeException("새 썸네일 파일 업로드 실패", e); // rollback 유도
            }
        }

        Data savedData = dataRepositoryPort.findDataById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));

        // 데이터셋 파일 파싱 후 통계 저장
        if (dataFile != null && !dataFile.isEmpty()) {
            kafkaProducerPort.sendUploadEvent(savedData.getId(), savedData.getDataFileUrl(), dataFile.getOriginalFilename());
        }

        log.info("데이터셋 수정 완료 - dataId: {}, title: {}", dataId, requestDto.title());
    }

    /**
     * 데이터셋을 삭제 상태로 표시하고 Elasticsearch 인덱스에서도 삭제 상태로 반영합니다.
     *
     * @param dataId 삭제할 데이터셋의 식별자
     */
    @Override
    @Transactional
    public void markAsDelete(Long dataId) {
        dataRepositoryPort.delete(dataId);
        dataIndexingPort.markAsDeleted(dataId);
    }

    /**
     * 데이터셋을 복구 상태로 변경하고, Elasticsearch 인덱스도 복구 상태로 동기화합니다.
     *
     * @param dataId 복구할 데이터셋의 식별자
     */
    @Override
    @Transactional
    public void markAsRestore(Long dataId) {
        dataRepositoryPort.restore(dataId);
        dataIndexingPort.markAsRestore(dataId);
    }
}
