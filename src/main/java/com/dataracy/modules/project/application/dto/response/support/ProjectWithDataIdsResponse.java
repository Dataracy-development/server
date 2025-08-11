package com.dataracy.modules.project.application.dto.response.support;

import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

/**
 + * 프로젝트와 데이터 ID 목록을 함께 전달하기 위한 내부 지원 DTO.
 + * 어댑터 → 서비스 계층 사이의 중간 전달용이며, 실제 API 응답 DTO가 아닙니다.
 + * @param project 프로젝트 도메인 모델
 + * @param dataIds 프로젝트에 연관된 데이터 ID 목록
 */
public record ProjectWithDataIdsResponse(
        Project project,
        List<Long> dataIds
) {}
