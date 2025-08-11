package com.dataracy.modules.dataset.application.dto.response.download;

/**
 *요청
 * @param preSignedUrl
 */
public record GetDataPreSignedUrlResponse(
    String preSignedUrl
) {}
