package com.dataracy.modules.project.domain.model;

import lombok.*;

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

    /**
     * 주어진 필드 값들로 새로운 Project 도메인 객체를 생성합니다.
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
     * @return 생성된 Project 도메인 객체
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
            String content
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
                .build();
    }
}
