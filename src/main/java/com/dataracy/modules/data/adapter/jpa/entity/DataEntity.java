package com.dataracy.modules.data.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
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
public class DataEntity extends BaseTimeEntity {
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
    private Long dataTypeId;

    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String analysisGuide;

    @Column
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

    /**
     * 데이터의 다운로드 횟수를 1 증가시킵니다.
     */
    public void increaseDownloadCount() {
        this.downloadCount++;
    }

    /**
     * 데이터셋 파일의 URL을 지정된 값으로 업데이트합니다.
     *
     * @param dataFileUrl 새로 설정할 데이터셋 파일의 URL
     */
    public void updateDataFile (String dataFileUrl) {
        this.dataFileUrl = dataFileUrl;
    }

    /**
     * 썸네일 파일의 URL을 새로운 값으로 업데이트합니다.
     *
     * @param thumbnailUrl 새로 설정할 썸네일 파일의 URL
     */
    public void updateThumbnailFile (String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * 데이터 엔티티의 메타데이터를 새로운 DataMetadataEntity 인스턴스로 변경합니다.
     *
     * @param metadata 새로 설정할 메타데이터 엔티티
     */
    public void updateMetadata (DataMetadataEntity metadata) {
        this.metadata = metadata;
    }

    /**
     * 주어진 값들로 새로운 DataEntity 객체를 생성합니다.
     *
     * @param title 데이터셋의 제목
     * @param topicId 주제 ID
     * @param userId 사용자 ID
     * @param dataSourceId 데이터 소스 ID
     * @param dataTypeId 데이터 유형 ID
     * @param startDate 데이터셋의 시작 날짜 (null 가능)
     * @param endDate 데이터셋의 종료 날짜 (null 가능)
     * @param description 데이터셋 설명
     * @param analysisGuide 분석 가이드
     * @param dataFileUrl 데이터 파일의 URL (null 가능)
     * @param thumbnailUrl 썸네일 이미지의 URL (null 가능)
     * @param downloadCount 전체 다운로드 횟수
     * @param recentWeekDownloadCount 최근 1주일간 다운로드 횟수
     * @param metadata 데이터 메타데이터 엔티티
     * @return 생성된 DataEntity 인스턴스
     */
    public static DataEntity of(
            String title,
            Long topicId,
            Long userId,
            Long dataSourceId,
            Long dataTypeId,
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
                .dataTypeId(dataTypeId)
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
