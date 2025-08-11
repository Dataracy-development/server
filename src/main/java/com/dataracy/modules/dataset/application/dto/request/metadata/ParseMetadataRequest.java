package com.dataracy.modules.dataset.application.dto.request.metadata;

/**
 * 메타데이터 파싱 애플리케이션 요청 DTO
 *
 * @param dataId 데이터 식별자
 * @param fileUrl 파일 URL
 * @param originalFilename 원본 파일명
 */
public record ParseMetadataRequest(
        Long dataId,
        String fileUrl,
        String originalFilename
) {}
