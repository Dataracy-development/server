package com.dataracy.modules.dataset.adapter.web.response.read;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "데이터셋 상세 정보 웹 응답 DTO")
public record DataDetailWebResponse(
        @Schema(description = "데이터셋 아이디", example = "1")
        Long id,

        @Schema(description = "데이터셋 제목", example = "최신 3년 이내 개발자 현황")
        String title,

        @Schema(description = "작성자 아이디", example = "1")
        Long creatorId,

        @Schema(description = "작성자 닉네임", example = "박준형")
        String creatorName,

        @Schema(description = "유저 프로필 이미지 URL", example = "https://www.s3.~~~")
        String userProfileImageUrl,

        @Schema(description = "유저 소개글", example = "안녕하세요. 주니입니다.")
        String userIntroductionText,

        @Schema(description = "작성자 유형 라벨", example = "실무자")
        String authorLabel,

        @Schema(description = "직업 라벨", example = "학생")
        String occupationLabel,

        @Schema(description = "토픽 라벨", example = "디자인")
        String topicLabel,

        @Schema(description = "데이터 출처 라벨", example = "한국정보협회")
        String dataSourceLabel,

        @Schema(description = "데이터 유형 라벨", example = "XLSX")
        String dataTypeLabel,

        @Schema(description = "시작일", example = "2025-08-01")
        LocalDate startDate,

        @Schema(description = "종료일", example = "2025-08-05")
        LocalDate endDate,

        @Schema(description = "데이터셋 설명", example = "이 데이터셋은 ~~~")
        String description,

        @Schema(description = "데이터셋 분석 가이드", example = "이 데이터셋 분석 방법은 이런 식으로 해주세요. ~~~")
        String analysisGuide,

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

        @Schema(description = "데이터셋 미리보기", example = "이 데이터셋 미리보기 문자열입니다. ~~")
        String previewJson,

        @Schema(description = "생성일", example = "2025-08-04T10:30:00")
        LocalDateTime createdAt
) {}
