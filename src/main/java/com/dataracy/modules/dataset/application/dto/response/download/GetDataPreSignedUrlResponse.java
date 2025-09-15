package com.dataracy.modules.dataset.application.dto.response.download;

/**
 * 데이터셋 파일의 다운로드 링크를 응답하는 애플리케이션 응답 DTO
 *
 * @param preSignedUrl 유효기간이 있는 다운로드 링크
 */
public record GetDataPreSignedUrlResponse(
    String preSignedUrl
) {}
