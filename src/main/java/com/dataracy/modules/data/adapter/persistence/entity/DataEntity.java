package com.dataracy.modules.data.adapter.persistence.entity;

import com.dataracy.modules.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * data 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "data"
)
public class DataEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    // 도메인 : 타 어그리거트이므로 id로 간접참조
    @Column(nullable = false)
    private Long topicId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long dataSourceId;
    @Column(nullable = false)
    private Long authorLevelId;

    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String analysisGuide;

    @Column(nullable = false)
    private String dataFileUrl;
    @Column
    private String thumbnailUrl;

    // 캐싱 필드
    @Column
    private int downloadCount;
    @Column
    private int recentWeekDownloadCount;

    // 메타데이터 FK (1:1)
    @OneToOne(mappedBy = "data", cascade = CascadeType.PERSIST)
    private DataMetadataEntity metadata;

    public void increaseDownloadCount() {
        this.downloadCount++;
    }

    /**
     * 데이터셋의 파일 URL을 새로운 값으로 변경합니다.
     *
     * @param dataFileUrl 새로 지정할 데이터셋 파일의 URL
     */
    public void updateDataFile (String dataFileUrl) {
        this.dataFileUrl = dataFileUrl;
    }

    public void updateThumbnailFile (String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public static DataEntity toEntity(
            String title,
            Long topicId,
            Long userId,
            Long dataSourceId,
            Long authorLevelId,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            String analysisGuide,
            String dataFileUrl,
            String thumbnailUrl,
            int downloadCount,
            int recentWeekDownloadCount,
            DataMetadataEntity metadata
    ) {
        return DataEntity.builder()
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .dataSourceId(dataSourceId)
                .authorLevelId(authorLevelId)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .analysisGuide(analysisGuide)
                .dataFileUrl(dataFileUrl)
                .thumbnailUrl(thumbnailUrl)
                .downloadCount(downloadCount)
                .recentWeekDownloadCount(recentWeekDownloadCount)
                .metadata(metadata)
                .build();
    }
}
