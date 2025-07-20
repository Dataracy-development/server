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
    private Long parentProjectId;
    private String content;
    private String fileUrl;
    // 타 어그리거트인 Data 자체를 직접 들고 있지 않고, ID만 보유해서 간접 참조
    private List<Long> dataIds;
    private LocalDateTime createdAt;

    private Long commentCount;
    private Long likeCount;
    private Long viewCount;

    private Boolean isDeleted;

    private List<Project> childProjects;

    /**
     * 프로젝트의 썸네일 URL을 업데이트합니다.
     *
     * @param fileUrl 새로 설정할 썸네일 URL
     */
    public void updateFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 모든 속성 값을 지정하여 새로운 Project 인스턴스를 생성합니다.
     *
     * @param childProjects 하위 프로젝트 목록을 포함하여 프로젝트 계층 구조를 설정할 때 사용합니다.
     * @return 지정된 모든 속성 값이 반영된 Project 객체
     */
    public static Project of(
            Long id,
            String title,
            Long topicId,
            Long userId,
            Long analysisPurposeId,
            Long dataSourceId,
            Long authorLevelId,
            Boolean isContinue,
            Long parentProjectId,
            String content,
            String fileUrl,
            List<Long> dataIds,
            LocalDateTime createdAt,
            Long commentCount,
            Long likeCount,
            Long viewCount,
            Boolean isDeleted,
            List<Project> childProjects
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
                .parentProjectId(parentProjectId)
                .content(content)
                .fileUrl(fileUrl)
                .dataIds(dataIds)
                .createdAt(createdAt)
                .commentCount(commentCount)
                .likeCount(likeCount)
                .viewCount(viewCount)
                .isDeleted(isDeleted)
                .childProjects(childProjects)
                .build();
    }
}
