package com.dataracy.modules.reference.adapter.web.response.singleview;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "도메인 단일 조회 응답")
public record TopicWebResponse(
    @Schema(description = "PK") Long id,
    @Schema(description = "표출 값") String value,
    @Schema(description = "라벨") String label) {}
