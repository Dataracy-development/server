package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 최신 데이터셋 도메인 DTO와 최신 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class RecentDataSetsDtoMapper {
    public DataMinimalSearchResponse toResponseDto(
            Data data
    ) {
        return new DataMinimalSearchResponse(
                data.getId(),
                data.getTitle(),
                data.getThumbnailUrl(),
                data.getCreatedAt()
        );
    }
}
