package com.dataracy.modules.dataset.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

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
@Where(clause = "is_deleted = false")
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
    private String dataThumbnailUrl;

    // 캐싱 필드
    @Column
    private int downloadCount;

    @Column
    private Long sizeBytes;

    // 메타데이터 FK (1:1)
    @OneToOne(mappedBy = "data", cascade = CascadeType.PERSIST)
    private DataMetadataEntity metadata;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * 엔티티를 논리적으로 삭제 처리합니다.
     *
     * 이 메서드는 isDeleted 플래그를 true로 설정하여 데이터가 삭제된 것으로 표시합니다.
     * 실제 데이터베이스에서 삭제되지는 않으며, @Where 절에 의해 쿼리 결과에서 제외됩니다.
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 논리적으로 삭제된 엔티티를 복구 상태로 전환합니다.
     */
    public void restore() {
        this.isDeleted = false;
    }

    /**
     * 주어진 수정 요청 DTO의 값으로 데이터 엔티티의 주요 필드를 일괄적으로 갱신합니다.
     *
     * @param requestDto 데이터의 새로운 값이 포함된 수정 요청 DTO
     */
    public void modify(ModifyDataRequest requestDto) {
        this.title = requestDto.title();
        this.topicId = requestDto.topicId();
        this.dataSourceId = requestDto.dataSourceId();
        this.dataTypeId = requestDto.dataTypeId();
        this.startDate = requestDto.startDate();
        this.endDate = requestDto.endDate();
        this.description = requestDto.description();
        this.analysisGuide = requestDto.analysisGuide();
    }

    /**
     * 데이터의 다운로드 횟수를 1 증가시킵니다.
     */
    public void increaseDownloadCount() {
        this.downloadCount++;
    }

    /**
     * 데이터셋 파일의 URL과 파일 크기(sizeBytes)를 검증하여 업데이트합니다.
     *
     * 새 URL이 null이거나 공백이면 DataException(DataErrorStatus.INVALID_FILE_URL)을 던집니다.
     * 전달된 URL이 현재와 동일하면 아무 작업도 하지 않습니다. 유효한 새 URL일 경우
     * dataFileUrl을 갱신하고 sizeBytes를 dataFileSize로 설정합니다.
     *
     * @param dataFileUrl  새로 설정할 데이터셋 파일의 URL (null 또는 공백이면 예외)
     * @param dataFileSize 새로 설정할 파일의 크기(바이트). URL이 동일하여 변경이 일어나지 않더라도 전달값이 무시될 수 있음
     * @throws DataException dataFileUrl이 null이거나 공백 문자열인 경우 발생
     */
    public void updateDataFile (String dataFileUrl, Long dataFileSize) {
        if (dataFileUrl == null || dataFileUrl.isBlank()) {
            LoggerFactory.domain().logWarning("잘못된 데이터셋 파일 url 형식입니다.");
            throw new DataException(DataErrorStatus.INVALID_FILE_URL);
        }
        if (dataFileUrl.equals(this.dataFileUrl)) {
            return;
        }
        this.dataFileUrl = dataFileUrl;
        this.sizeBytes = dataFileSize;
    }

    /**
     * 데이터셋 썸네일 파일의 URL을 검증하여 유효할 경우 해당 값으로 갱신합니다.
     *
     * @param thumbnailUrl 새로 설정할 썸네일 파일의 URL. null이거나 공백일 경우 예외가 발생합니다.
     * @throws DataException thumbnailUrl이 null이거나 공백일 때 발생합니다.
     */
    public void updateDataThumbnailFile (String thumbnailUrl) {
        if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
            LoggerFactory.domain().logWarning("잘못된 데이터셋 썸네일 파일 url 형식입니다.");
            throw new DataException(DataErrorStatus.INVALID_FILE_URL);
        }
        if (thumbnailUrl.equals(this.dataThumbnailUrl)) {
            return;
        }
        this.dataThumbnailUrl = thumbnailUrl;
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
     * 주어진 값들로 새로운 DataEntity 인스턴스를 생성하여 반환합니다.
     *
     * <p>데이터셋의 식별·분류 정보, 설명 및 연관 메타데이터를 초기화한 엔티티를 빌더로 생성합니다.</p>
     *
     * @param startDate 데이터셋 시작일 (null 허용)
     * @param endDate 데이터셋 종료일 (null 허용)
     * @param dataFileUrl 데이터 파일의 URL (null 허용)
     * @param dataThumbnailUrl 썸네일 이미지의 URL (null 허용)
     * @param sizeBytes 데이터 파일의 크기(바이트 단위). 파일 크기를 모를 경우 null 허용
     * @param metadata 연관된 DataMetadataEntity
     * @return 초기화된 DataEntity 인스턴스
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
            String dataThumbnailUrl,
            int downloadCount,
            Long sizeBytes,
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
                .dataThumbnailUrl(dataThumbnailUrl)
                .downloadCount(downloadCount)
                .sizeBytes(sizeBytes)
                .metadata(metadata)
                .build();
    }
}
