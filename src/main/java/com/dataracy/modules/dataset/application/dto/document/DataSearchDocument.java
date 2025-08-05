package com.dataracy.modules.dataset.application.dto.document;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabels;
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
        Integer rowCount,
        Integer columnCount,
        String previewJson,
        LocalDateTime createdAt,
        Boolean isDeleted
) {
    /**
     * 데이터, 메타데이터, 라벨 정보를 결합하여 검색 및 색인에 적합한 DataSearchDocument 인스턴스를 생성합니다.
     *
     * @param data 데이터의 주요 속성을 포함하는 객체
     * @param dataMetadata 데이터의 행/열 수 및 미리보기 정보를 담은 객체
     * @param dataLabels 데이터와 관련된 라벨 정보를 제공하는 DTO
     * @return 검색 엔진 색인에 사용할 DataSearchDocument 인스턴스
     */
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
                .rowCount(dataMetadata.getRowCount())
                .columnCount(dataMetadata.getColumnCount())
                .previewJson(dataMetadata.getPreviewJson())
                .createdAt(data.getCreatedAt())
                .isDeleted(false)
                .build();
    }
}
