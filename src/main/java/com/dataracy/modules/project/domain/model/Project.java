package com.dataracy.modules.project.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project {
    private Long id;

    private String title;
    private Long userId;

    private Long analysisPurposeId;
    private Long dataSourceId;
    private Long authorLevelId;
    private Long topicId;

    private Boolean isContinue;
    private Long parentProjectId;
    private String content;
    private String thumbnailUrl;

    // 타 어그리거트인 Data 자체를 직접 들고 있지 않고, ID만 보유해서 간접 참조
    private List<Long> dataIds;

    private Long commentCount;
    private Long likeCount;
    private Long viewCount;

    private List<Project> childProjects;

    private Boolean isDeleted;
    private LocalDateTime createdAt;

    /**
     * 프로젝트의 썸네일 이미지를 새로운 URL로 변경합니다.
     *
     * @param thumbnailUrl 변경할 썸네일 이미지의 URL
     */
    public void updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * 모든 속성 값을 지정하여 새로운 Project 인스턴스를 생성합니다.
     *
     * @param id 프로젝트의 고유 식별자
     * @param title 프로젝트 제목
     * @param topicId 연관된 토픽의 식별자
     * @param userId 프로젝트 소유자 또는 생성자의 식별자
     * @param analysisPurposeId 분석 목적의 식별자
     * @param dataSourceId 데이터 소스의 식별자
     * @param authorLevelId 작성자 레벨의 식별자
     * @param isContinue 프로젝트의 진행 여부
     * @param parentProjectId 상위 프로젝트의 식별자
     * @param content 프로젝트 설명 또는 내용
     * @param thumbnailUrl 프로젝트 썸네일 이미지의 URL
     * @param dataIds 연관된 데이터 엔티티의 식별자 목록
     * @param createdAt 프로젝트 생성 시각
     * @param commentCount 댓글 수
     * @param likeCount 좋아요 수
     * @param viewCount 조회 수
     * @param isDeleted 삭제 여부
     * @param childProjects 하위 프로젝트 목록
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
            String thumbnailUrl,
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
                .thumbnailUrl(thumbnailUrl)
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
