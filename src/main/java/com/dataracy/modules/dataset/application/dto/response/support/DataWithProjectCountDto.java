package com.dataracy.modules.dataset.application.dto.response.support;

import com.dataracy.modules.dataset.domain.model.Data;

/**
 * 데이터 엔터티와 해당 데이터와 연결된 프로젝트의 개수를 포함하는 보조 응답 DTO
 *
 * @param data 데이터 도메인 객체
 * @param countConnectedProjects 연결된 프로젝트 수
 */
public record DataWithProjectCountDto(
        Data data,
        Long countConnectedProjects
) {}
