package com.dataracy.modules.dataset.application.dto.request.search;

/**
 * 데이터셋 필터링 애플리케이션 요청 DTO
 * @param keyword 키우더
 * @param sortType 정렬 유형
 * @param topicId 주제 아이디
 * @param dataSourceId  데이터 소스 ID
 * @param dataTypeId
 * @param year
 */
public record FilteringDataRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        Integer year
) {}
