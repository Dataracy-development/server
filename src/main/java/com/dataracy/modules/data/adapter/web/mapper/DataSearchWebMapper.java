package com.dataracy.modules.data.adapter.web.mapper;

import com.dataracy.modules.data.adapter.web.response.DataSimilarSearchWebResponse;
import com.dataracy.modules.data.application.dto.response.DataSimilarSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class DataSearchWebMapper {
    public DataSimilarSearchWebResponse toWebDto(DataSimilarSearchResponse responseDto) {
        return new DataSimilarSearchWebResponse(
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
                responseDto.createdAt()
        );
    }
}
