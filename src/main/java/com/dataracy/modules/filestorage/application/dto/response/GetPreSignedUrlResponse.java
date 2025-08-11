package com.dataracy.modules.filestorage.application.dto.response;

/**
 * Pre-signed URL 발급 응답 DTO
 *
 * @param preSignedUrl 사전 서명 URL
 */
public record GetPreSignedUrlResponse(
        String preSignedUrl
) {}
