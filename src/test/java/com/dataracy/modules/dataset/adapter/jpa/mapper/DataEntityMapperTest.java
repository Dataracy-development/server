package com.dataracy.modules.dataset.adapter.jpa.mapper;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DataEntityMapper 테스트")
class DataEntityMapperTest {

    @Test
    @DisplayName("Entity를 Domain으로 변환 성공")
    void toDomain_ShouldConvertEntityToDomain() {
        // Given
        DataMetadataEntity metadataEntity = DataMetadataEntity.of(1000, 10, "{\"preview\": \"data\"}");
        DataEntity entity = DataEntity.builder()
                .id(1L)
                .title("Test Data")
                .topicId(1L)
                .userId(1L)
                .dataSourceId(1L)
                .dataTypeId(1L)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .description("Test description")
                .analysisGuide("Test guide")
                .dataFileUrl("file.csv")
                .dataThumbnailUrl("thumb.jpg")
                .downloadCount(100)
                .sizeBytes(1024L)
                .metadata(metadataEntity)
                .build();

        // When
        Data domain = DataEntityMapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getTitle()).isEqualTo("Test Data");
        assertThat(domain.getTopicId()).isEqualTo(1L);
        assertThat(domain.getUserId()).isEqualTo(1L);
        assertThat(domain.getDataSourceId()).isEqualTo(1L);
        assertThat(domain.getDataTypeId()).isEqualTo(1L);
        assertThat(domain.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(domain.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(domain.getDescription()).isEqualTo("Test description");
        assertThat(domain.getAnalysisGuide()).isEqualTo("Test guide");
        assertThat(domain.getDataFileUrl()).isEqualTo("file.csv");
        assertThat(domain.getDataThumbnailUrl()).isEqualTo("thumb.jpg");
        assertThat(domain.getDownloadCount()).isEqualTo(100);
        assertThat(domain.getSizeBytes()).isEqualTo(1024L);
        assertThat(domain.getMetadata()).isNotNull();
    }

    @Test
    @DisplayName("Entity가 null일 때 Domain 변환 시 null 반환")
    void toDomain_WithNullEntity_ShouldReturnNull() {
        // When
        Data domain = DataEntityMapper.toDomain(null);

        // Then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("Entity에 메타데이터가 null일 때 Domain 변환 성공")
    void toDomain_WithNullMetadata_ShouldConvertSuccessfully() {
        // Given
        DataEntity entity = DataEntity.builder()
                .id(1L)
                .title("Test Data")
                .topicId(1L)
                .userId(1L)
                .dataSourceId(1L)
                .dataTypeId(1L)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .description("Test description")
                .analysisGuide("Test guide")
                .dataFileUrl("file.csv")
                .dataThumbnailUrl("thumb.jpg")
                .downloadCount(100)
                .sizeBytes(1024L)
                .metadata(null)
                .build();

        // When
        Data domain = DataEntityMapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Domain을 Entity로 변환 성공")
    void toEntity_ShouldConvertDomainToEntity() {
        // Given
        DataMetadata metadata = DataMetadata.of(1L, 1000, 10, "{\"preview\": \"data\"}");
        Data domain = Data.builder()
                .id(1L)
                .title("Test Data")
                .topicId(1L)
                .userId(1L)
                .dataSourceId(1L)
                .dataTypeId(1L)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .description("Test description")
                .analysisGuide("Test guide")
                .dataFileUrl("file.csv")
                .dataThumbnailUrl("thumb.jpg")
                .downloadCount(100)
                .sizeBytes(1024L)
                .metadata(metadata)
                .build();

        // When
        DataEntity entity = DataEntityMapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Test Data");
        assertThat(entity.getTopicId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(1L);
        assertThat(entity.getDataSourceId()).isEqualTo(1L);
        assertThat(entity.getDataTypeId()).isEqualTo(1L);
        assertThat(entity.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(entity.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(entity.getDescription()).isEqualTo("Test description");
        assertThat(entity.getAnalysisGuide()).isEqualTo("Test guide");
        assertThat(entity.getDataFileUrl()).isEqualTo("file.csv");
        assertThat(entity.getDataThumbnailUrl()).isEqualTo("thumb.jpg");
        assertThat(entity.getDownloadCount()).isEqualTo(100);
        assertThat(entity.getSizeBytes()).isEqualTo(1024L);
        assertThat(entity.getMetadata()).isNotNull();
    }

    @Test
    @DisplayName("Domain이 null일 때 Entity 변환 시 null 반환")
    void toEntity_WithNullDomain_ShouldReturnNull() {
        // When
        DataEntity entity = DataEntityMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }
}