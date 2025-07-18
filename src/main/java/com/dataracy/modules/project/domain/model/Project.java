package com.dataracy.modules.project.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 프로젝트 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project {

    private Long id;
    private String title;
    private Long topicId;
    private Long userId;
    private Long analysisPurposeId;
    private Long dataSourceId;
    private Long authorLevelId;
    private Boolean isContinue;
    private Project parentProject;
    private String content;
    private String fileUrl;
    // 타 어그리거트인 Data 자체를 직접 들고 있지 않고, ID만 보유해서 간접 참조
    private List<Long> dataIds;
    private LocalDateTime createdAt;

    /**
     * 프로젝트의 썸네일 URL을 업데이트합니다.
     *
     * @param fileUrl 새로 설정할 썸네일 URL
     */
    public void updateFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 주어진 값들로 새로운 Project 도메인 객체를 생성하여 반환합니다.
     *
     * @param id 프로젝트의 고유 식별자
     * @param title 프로젝트 제목
     * @param topicId 주제 식별자
     * @param userId 사용자 식별자
     * @param analysisPurposeId 분석 목적 식별자
     * @param dataSourceId 데이터 소스 식별자
     * @param authorLevelId 작성자 등급 식별자
     * @param isContinue 프로젝트의 연속 여부
     * @param parentProject 상위 프로젝트 객체
     * @param content 프로젝트 내용
     * @param fileUrl 프로젝트 썸네일 또는 파일 URL
     * @param dataIds 연관된 데이터셋의 식별자 리스트
     * @param createdAt 프로젝트 생성 시각
     * @return 생성된 Project 객체
     */
    public static Project toDomain(
            Long id,
            String title,
            Long topicId,
            Long userId,
            Long analysisPurposeId,
            Long dataSourceId,
            Long authorLevelId,
            Boolean isContinue,
            Project parentProject,
            String content,
            String fileUrl,
            List<Long> dataIds,
            LocalDateTime createdAt
    ) {
        return Project.builder()
                .id(id)
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .analysisPurposeId(analysisPurposeId)
                .dataSourceId(dataSourceId)
                .authorLevelId(authorLevelId)
                .isContinue(isContinue)
                .parentProject(parentProject)
                .content(content)
                .fileUrl(fileUrl)
                .dataIds(dataIds)
                .createdAt(createdAt)
                .build();
    }
}
