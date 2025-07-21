package com.dataracy.modules.dataset.adapter.elasticsearch.document;

import com.dataracy.modules.dataset.application.dto.response.DataLabels;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record DataSearchDocument(
        Long id,
        String title,
        Long topicId,
        String topicLabel,
        Long userId,
        String username,
        Long dataSourceId,
        String dataSourceLabel,
        Long dataTypeId,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide,
        String dataFileUrl,
        String thumbnailUrl,
        int downloadCount,
        int recentWeekDownloadCount,
        Integer rowCount,
        Integer columnCount,
        String previewJson,
        LocalDateTime createdAt
) {
    public static DataSearchDocument from(
            Data data,
            DataMetadata dataMetadata,
            DataLabels dataLabels
    ) {
        return DataSearchDocument.builder()
                .id(data.getId())
                .title(data.getTitle())
                .topicId(data.getTopicId())
                .topicLabel(dataLabels.topicLabel())
                .userId(data.getUserId())
                .username(dataLabels.username())
                .dataSourceId(data.getDataSourceId())
                .dataSourceLabel(dataLabels.dataSourceLabel())
                .dataTypeId(data.getDataTypeId())
                .dataTypeLabel(dataLabels.dataTypeLabel())
                .startDate(data.getStartDate())
                .endDate(data.getEndDate())
                .description(data.getDescription())
                .analysisGuide(data.getAnalysisGuide())
                .dataFileUrl(data.getDataFileUrl())
                .thumbnailUrl(data.getThumbnailUrl())
                .downloadCount(data.getDownloadCount())
                .recentWeekDownloadCount(data.getRecentWeekDownloadCount())
                .rowCount(dataMetadata.getRowCount())
                .columnCount(dataMetadata.getColumnCount())
                .previewJson(dataMetadata.getPreviewJson())
                .createdAt(data.getCreatedAt())
                .build();
    }
}
