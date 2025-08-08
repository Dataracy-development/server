package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import org.springframework.stereotype.Component;

@Component
public class DataSearchWebMapper {
    /**
     * 도메인 계층의 SimilarDataResponse 객체를 웹 계층의 SimilarDataWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 SimilarDataResponse 도메인 DTO
     * @return 변환된 SimilarDataWebResponse 웹 응답 DTO
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
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt()
        );
    }

    /**
     * 최근 최소 데이터 도메인 DTO를 웹 응답 DTO로 변환합니다.
     *
     * @param responseDto 변환할 최근 최소 데이터 도메인 DTO
     * @return 변환된 최근 최소 데이터 웹 응답 DTO
     */
    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.dataThumbnailUrl(),
                responseDto.createdAt()
        );
    }
}
