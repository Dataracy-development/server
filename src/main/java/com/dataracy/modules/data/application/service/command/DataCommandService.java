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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        // 프로젝트 업로드 DB 저장
        Data data = Data.toDomain(
                null,
                requestDto.title(),
                requestDto.topicId(),
                userId,
                requestDto.dataSourceId(),
                requestDto.authorLevelId(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                requestDto.analysisGuide(),
                null,
                null,
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
