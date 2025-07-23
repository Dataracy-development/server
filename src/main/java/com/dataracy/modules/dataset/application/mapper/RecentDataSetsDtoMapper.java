package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 최신 데이터셋 도메인 DTO와 최신 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class RecentDataSetsDtoMapper {
    /**
     * 도메인 모델 Data 객체를 DataMinimalSearchResponse DTO로 변환합니다.
     *
     * @param data 변환할 Data 객체
     * @return DataMinimalSearchResponse로 매핑된 DTO 객체
     */
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
