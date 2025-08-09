package com.dataracy.modules.project.adapter.web.response.read;

import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "프로젝트 상세 정보 웹 응답 DTO")
public record ProjectDetailWebResponse(
        @Schema(description = "프로젝트 아이디", example = "1")
        Long id,

        @Schema(description = "프로젝트 제목", example = "디자인에 관하여")
        String title,

        @Schema(description = "작성자명", example = "박준형")
        String username,

        @Schema(description = "작성자 소개글", example = "안녕하세요. 박준형입니다.")
        String userIntroductionText,

        @Schema(description = "작성자 유형 라벨", example = "실무자")
        String authorLevelLabel,

        @Schema(description = "직업 라벨", example = "디자이너")
        String occupationLabel,

        @Schema(description = "토픽 라벨", example = "디자인")
        String topicLabel,

        @Schema(description = "분석 목적 라벨", example = "디자인 통계 데이터에 대한 분석을 위하여")
        String analysisPurposeLabel,

        @Schema(description = "데이터 출처 라벨", example = "한국데이터협회")
        String dataSourceLabel,

        @Schema(description = "이어가기 프로젝트인지 여부", example = "false")
        boolean isContinue,

        @Schema(description = "부모 프로젝트 아이디", example = "3")
        Long parentProjectId,

        @Schema(description = "분석 내용", example = "디자인과 관련된 분석 내용은 ~~")
        String content,

        @Schema(description = "프로젝트 썸네일 url", example = "https://www.s3.~~~")
        String projectThumbnailUrl,

        @Schema(description = "생성일", example = "2025-08-04T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "댓글 수", example = "1")
        Long commentCount,

        @Schema(description = "좋아요 수", example = "1")
        Long likeCount,

        @Schema(description = "조회 수", example = "100")
        Long viewCount,

        @Schema(description = "좋아요 여부", example = "false")
        boolean isLiked,

        @Schema(description = "자식 프로젝트 존재 유무", example = "false")
        boolean hasChild,

        @Schema(description = "연결된 데이터셋 목록")
        List<ProjectConnectedDataWebResponse> connectedDataSets
) {}
