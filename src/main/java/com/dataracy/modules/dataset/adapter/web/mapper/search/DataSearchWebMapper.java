package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import org.springframework.stereotype.Component;

/**
 * 데이터 검색 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class DataSearchWebMapper {
    /**
     * SimilarDataResponse 애플리케이션 응답 DTO를 SimilarDataWebResponse 웹 응답 DTO로 변환합니다.
     *
     * @param responseDto 변환할 애플리케이션 DTO
     * @return 변환된 웹 응답 DTO
     */
    public SimilarDataWebResponse toWebDto(SimilarDataResponse responseDto) {
        return new SimilarDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.sizeBytes(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt()
        );
    }

    /**
     * 최근 최소 데이터 애플리케이션 DTO를 웹 계층의 응답 DTO로 변환합니다.
     *
     * @param responseDto 변환할 최근 최소 데이터 애플리케이션 응답 DTO
     * @return id, 제목, 썸네일 URL, 생성일시 정보를 포함하는 웹 응답 DTO
     */
    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.dataThumbnailUrl(),
                responseDto.createdAt()
        );
    }
}
