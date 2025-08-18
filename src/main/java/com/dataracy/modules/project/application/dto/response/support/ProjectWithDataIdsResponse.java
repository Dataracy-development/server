package com.dataracy.modules.project.application.dto.response.support;

import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

/**
 * 프로젝트와 데이터 ID 목록을 함께 전달하기 위한 보조 응답 DTO
 *
 * @param project 프로젝트 도메인 모델
 * @param dataIds 프로젝트에 연관된 데이터 ID 목록
 */
public record ProjectWithDataIdsResponse(
        Project project,
        List<Long> dataIds
) {}
