package com.dataracy.modules.dataset.application.dto.response.metadata;

/**
 * 메타데이터 파싱 애플리케이션 응답 DTO
 *
 * @param rowCount 데이터셋 파일 행
 * @param columnCount 데이터셋 파일 열
 * @param previewJson 데이터셋 파일 미리보기
 */
public record ParsedMetadataResponse(int rowCount, int columnCount, String previewJson) {}
