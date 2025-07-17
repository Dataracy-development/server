package com.dataracy.modules.data.application.service.command;

import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import com.dataracy.modules.data.application.port.in.DataUploadUseCase;
import com.dataracy.modules.data.application.port.out.DataKafkaProducerPort;
import com.dataracy.modules.data.application.port.out.DataRepositoryPort;
import com.dataracy.modules.data.domain.exception.DataException;
import com.dataracy.modules.data.domain.model.Data;
import com.dataracy.modules.data.domain.status.DataErrorStatus;
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
public class DataCommandService implements DataUploadUseCase {
    private final DataRepositoryPort dataRepositoryPort;
    private final DataKafkaProducerPort kafkaProducerPort;

    private final FileUploadUseCase fileUploadUseCase;
    private final ValidateTopicUseCase validateTopicUseCase;
    private final ValidateDataSourceUseCase validateDataSourceUseCase;
    private final ValidateDataTypeUseCase validateDataTypeUseCase;

    @Value("${default.image.url:}")
    private String defaultImageUrl;

    /**
     * 데이터셋 파일과 썸네일 파일을 검증 및 업로드하고, 데이터셋 정보를 저장한 뒤 업로드 이벤트를 발행합니다.
     *
     * 데이터셋 메타데이터와 파일의 유효성을 검사하고, 주제/데이터소스/데이터유형 ID를 각각 검증합니다.
     * 데이터셋 정보는 데이터베이스에 저장되며, 파일 업로드가 성공하면 해당 URL이 데이터셋에 반영됩니다.
     * 파일 업로드 실패 시 트랜잭션이 롤백되며, 데이터셋 파일 업로드가 완료되면 업로드 이벤트가 발행됩니다.
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
        Data data = Data.toDomain(
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
}
