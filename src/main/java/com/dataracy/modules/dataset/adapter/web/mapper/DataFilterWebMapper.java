package com.dataracy.modules.dataset.adapter.web.mapper;

import com.dataracy.modules.dataset.adapter.web.response.DataFilterWebResponse;
import com.dataracy.modules.dataset.application.dto.response.DataFilterResponse;
import org.springframework.stereotype.Component;

@Component
public class DataFilterWebMapper {
    /**
     * DataFilterResponse 객체를 DataFilterWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 DataFilterResponse 객체
     * @return 변환된 DataFilterWebResponse 객체
     */
    public DataFilterWebResponse toWebDto(DataFilterResponse responseDto) {
        return new DataFilterWebResponse(
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
                responseDto.recentWeekDownloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
