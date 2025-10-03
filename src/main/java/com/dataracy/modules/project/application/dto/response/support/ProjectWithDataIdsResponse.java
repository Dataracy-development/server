/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.dto.response.support;

import java.util.List;

import com.dataracy.modules.project.domain.model.Project;

/**
 * 프로젝트와 데이터 ID 목록을 함께 전달하기 위한 보조 DTO. 주의: 이 타입은 어댑터 ↔ 서비스 간 내부 전달용이며, API(Web) 응답 DTO가 아닙니다.
 *
 * @param project 프로젝트 도메인 모델
 * @param dataIds 프로젝트에 연관된 데이터 ID 목록
 */
public record ProjectWithDataIdsResponse(Project project, List<Long> dataIds) {}
