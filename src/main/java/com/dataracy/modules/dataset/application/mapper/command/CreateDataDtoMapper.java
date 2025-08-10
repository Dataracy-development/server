package com.dataracy.modules.dataset.application.mapper.command;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Data 도메인 요청 DTO -> Data 도메인 모델
 */
@Component
public class CreateDataDtoMapper {
    @Value("${default.dataset.image-url}")
    private String defaultDatasetImageUrl;

    /**
     * 업로드 데이터 요청 DTO와 사용자 ID를 기반으로 Data 도메인 객체를 생성합니다.
     *
     * @param requestDto 업로드할 데이터의 정보를 담은 요청 DTO
     * @param userId 데이터 소유자의 사용자 ID
     * @return 생성된 Data 도메인 객체
     */
    public Data toDomain(
            UploadDataRequest requestDto,
            Long userId
    ) {
        return Data.of(
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
                defaultDatasetImageUrl,
                0,
                null,
                null
        );
    }
}
