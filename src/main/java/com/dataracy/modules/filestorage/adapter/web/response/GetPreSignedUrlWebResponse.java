package com.dataracy.modules.filestorage.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 다운로드를 위한 일시적 url 반환 웹 응답 DTO")
public record GetPreSignedUrlWebResponse(
    @Schema(description = "유효기간이 있는 다운로드 링크", example = "https://~~~~")
    String preSignedUrl
){}
