package com.dataracy.modules.dataset.application.mapper.command;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 도메인 요청 DTO -> 도메인 모델
 */
@Component
public class UploadedDataDtoMapper {
    public Data toDomain(
            UploadDataRequest requestDto,
            Long userId,
            String defaultImageUrl
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
                defaultImageUrl,
                0,
                null,
                null
        );
    }
}
