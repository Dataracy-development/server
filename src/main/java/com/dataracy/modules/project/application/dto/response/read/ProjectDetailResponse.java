package com.dataracy.modules.project.application.dto.response.read;

import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 프로젝트 상세 조회 응답 DTO
 *
 * @param id 프로젝트 ID
 * @param title 프로젝트 제목
 * @param username 유저명
 * @param userIntroductionText 유저 자기소개
 * @param authorLevelLabel 작성자 유형 라벨
 * @param occupationLabel 직업 라벨
 * @param topicLabel 토픽 라벨
 * @param analysisPurposeLabel 분석 목적 라벨
 * @param dataSourceLabel 데이터 출처 라벨
 * @param isContinue 이어가기 여부
 * @param parentProjectId 부모 프로젝트 아이디
 * @param content 내용
 * @param projectThumbnailUrl 프로젝트 썸네일
 * @param createdAt 작성일
 * @param commentCount 댓글수
 * @param likeCount 좋아요수
 * @param viewCount 조회수
 * @param isLiked 좋아요 여부
 * @param hasChild 자식 프로젝트 존재 여부
 * @param connectedDataSets 연결된 데이터셋 목록
 */
public record ProjectDetailResponse(
        Long id,
        String title,
        String username,
        String userIntroductionText,
        String authorLevelLabel,
        String occupationLabel,
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        boolean isContinue,
        Long parentProjectId,
        String content,
        String projectThumbnailUrl,
        LocalDateTime createdAt,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        boolean isLiked,
        boolean hasChild,
        List<ProjectConnectedDataResponse> connectedDataSets
) {}
