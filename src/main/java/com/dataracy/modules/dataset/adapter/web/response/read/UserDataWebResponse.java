package com.dataracy.modules.dataset.adapter.web.response.read;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "로그인한 회원이 업로드한 데이터셋 웹 응답 DTO")
public record UserDataWebResponse(
        @Schema(description = "데이터셋 아이디", example = "1")
        Long id,

        @Schema(description = "데이터셋 제목", example = "최신 3년 이내 개발자 현황")
        String title,

        @Schema(description = "토픽 라벨", example = "디자인")
        String topicLabel,

        @Schema(description = "데이터 유형 라벨", example = "XLSX")
        String dataTypeLabel,

        @Schema(description = "시작일", example = "2025-08-01")
        LocalDate startDate,

        @Schema(description = "종료일", example = "2025-08-05")
        LocalDate endDate,

        @Schema(description = "데이터셋 썸네일 url", example = "https://www.s3.~~~")
        String dataThumbnailUrl,

        @Schema(description = "데이터셋 다운로드 횟수", example = "3")
        Integer downloadCount,

        @Schema(description = "데이터셋 파일 크기", example = "1048576")
        Long sizeBytes,

        @Schema(description = "데이터셋 행 수", example = "55")
        Integer rowCount,

        @Schema(description = "데이터셋 열 수", example = "100")
        Integer columnCount,

        @Schema(description = "생성일", example = "2025-08-04T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "연결된 프로젝트 개수", example = "5")
        Long countConnectedProjects
) {}
