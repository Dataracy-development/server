package com.dataracy.modules.dataset.application.mapper.command;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 도메인 요청 DTO -> 도메인 모델
 */
@Component
public class UploadedDataDtoMapper {
    /**
     * UploadDataRequest DTO와 추가 정보를 사용하여 Data 도메인 객체로 변환합니다.
     *
     * @param requestDto 업로드 데이터 요청 정보를 담은 DTO
     * @param userId 데이터 소유자의 사용자 ID
     * @param defaultImageUrl 데이터에 기본으로 할당할 이미지 URL
     * @return 변환된 Data 도메인 객체
     */
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
