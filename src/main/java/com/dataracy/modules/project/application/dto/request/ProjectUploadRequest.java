package com.dataracy.modules.project.application.dto.request;

/**
 * 프로젝트 업로드 도메인 요청 DTO
 * @param title 제목
 * @param topicId 도메인 아이디
 * @param analysisPurposeId 분석 목적 아이디
 * @param dataSourceId 데이터 출처 아이디
 * @param authorLevelId 작성자 유형 아이디
 * @param isContinue 이어가기 유무
 * @param parentProjectId 이어가기 프로젝트 아이디
 * @param content 내용
 */
public record ProjectUploadRequest(
        String title,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId,
        Boolean isContinue,
        Long parentProjectId,
        String content
) {}
