package com.dataracy.modules.dataset.application.dto.response.support;

/**
 * 주제/데이터소스/데이터유형 라벨과 사용자명을 포함하는 데이터 라벨 정보 보조 응답 DTO
 *
 * @param topicLabel 주제 라벨
 * @param dataSourceLabel 데이터 소스 라벨
 * @param dataTypeLabel 데이터 유형 라벨
 * @param username 사용자명
 * @param userProfileImageUrl 유저 프로필 이미지 URL
 */
public record DataLabels(
    String topicLabel,
    String dataSourceLabel,
    String dataTypeLabel,
    String username,
    String userProfileImageUrl) {}
