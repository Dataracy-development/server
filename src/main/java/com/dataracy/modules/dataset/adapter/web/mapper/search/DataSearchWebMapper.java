package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class DataSearchWebMapper {
    /**
     * 도메인 계층의 DataSimilarSearchResponse 객체를 웹 응답용 DataSimilarSearchWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 도메인 DTO 객체
     * @return 변환된 웹 응답 DTO 객체
     */
    public SimilarDataWebResponse toWebDto(SimilarDataResponse responseDto) {
        return new SimilarDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt()
        );
    }

    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.thumbnailUrl(),
                responseDto.createdAt()
        );
    }
}
