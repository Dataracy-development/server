package com.dataracy.modules.filestorage.application.dto.response;

/**
 *요청
 * @param preSignedUrl
 */
public record GetPreSignedUrlResponse(
        String preSignedUrl
) {}
