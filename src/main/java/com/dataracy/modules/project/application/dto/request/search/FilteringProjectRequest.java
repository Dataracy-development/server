package com.dataracy.modules.project.application.dto.request.search;

/**
 * 프로젝트 검색을 위한 필터 애플리케이션 요청 DTO
 *
 * @param keyword 키워드 검색어(선택)
 * @param sortType 정렬 기준(예: 최신순 등, 선택)
 * @param topicId 주제 ID(선택)
 * @param analysisPurposeId 분석 목적 ID(선택)
 * @param dataSourceId 데이터 소스 ID(선택)
 * @param authorLevelId 작성자 레벨 ID(선택)
 */
public record FilteringProjectRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId
) {}
